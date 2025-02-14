package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RepeatCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.gvf.HeadingType.Companion.constant
import org.ftc3825.gvf.followPath
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Drawing
import org.ftc3825.util.degrees
import org.ftc3825.util.geometry.DrivePowers
import org.ftc3825.util.geometry.Pose2D
import kotlin.math.PI

@Autonomous(name = "auto")
class Auto: CommandOpMode() {
    override fun initialize() {
        arrayListOf(
            Extendo, Telemetry, Drivetrain,
            OuttakeArm, OuttakeClaw, SampleIntake
        ).forEach { it.reset() }

        Drivetrain.pinpoint.resetPosAndIMU()
        val robotStart = Pose2D(9, -66, PI / 2)
        Drivetrain.position = robotStart

        val hangPreload = (
            Command.parallel(
                OuttakeClaw.grab(),
                OuttakeClaw.rollUp(),
                OuttakeClaw.outtakePitch(),
                SampleIntake.release(),
                SampleIntake.rollCenter(),
                SampleIntake.pitchDown(),
                WaitCommand(0.1)
            ) andThen ( followPath {
                start(robotStart.vector)
                lineTo(-1, -30, constant(PI / 2))
            } parallelTo OuttakeArm.outtakeAngle() )
            andThen ( OuttakeArm.runToPosition(degrees(140)) withTimeout(0.3) )
            andThen ( Drivetrain.run {
                it.setWeightedDrivePower(
                    drive = 0.0,
                    strafe = 1.0,
                    turn = 0.0
                )
            } withTimeout(0.2)
            withEnd {
                Drivetrain.setWeightedDrivePower(DrivePowers())
            } )
            andThen OuttakeClaw.release()
            andThen WaitCommand(0.3)
                andThen ( Drivetrain.run {
                it.setWeightedDrivePower(
                    drive = -1.0,
                    strafe = 0.0,
                    turn = 0.0
                )
            } withTimeout(0.2)
                withEnd {
                Drivetrain.setWeightedDrivePower(DrivePowers())
            } )
            andThen ( followPath {
                start(3, -35)
                curveTo(
                    0, -20,
                    0, 40,
                    39, -12,
                    constant(PI / 2)
                )
            } parallelTo ( OuttakeArm.wallAngle() withTimeout(3) ) )
            andThen Command.parallel(
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.release()
            )
            andThen followPath {
                start(39, -12)
                lineTo(41, -12, constant(PI / 2)) // behind first
                lineTo(46, -50, constant(PI / 2)) // push
                lineTo(46, -12, constant(PI / 2)) // back
            }
            andThen followPath {
                start(46, -12)
                lineTo(52, -12, constant(PI / 2)) // behind second
                lineTo(58, -60, constant(PI / 2)) // push
            }
            andThen followPath {
                start(58, -60)
                curveTo(
                    0, 10,
                    0, -10,
                    48, -65,
                    constant(PI / 2)
                )
            }
        )
        val cycle = (
            RepeatCommand( times = 3, command = (
                OuttakeClaw.grab()
                    andThen WaitCommand(0.5)
                    andThen Command.parallel(
                    OuttakeClaw.outtakePitch(),
                    OuttakeArm.outtakeAngle(),
                    WaitCommand(0.15) andThen OuttakeClaw.rollUp(),
                )
                andThen followPath {
                    start(48, -66)
                    curveTo(
                        0, 40,
                        0, 40,
                        -2, -30,
                        constant(PI / 2)
                    )
                }
                andThen (
                OuttakeArm.runToPosition(degrees(140))
                    withTimeout (0.5)
                )
                andThen ( Drivetrain.run {
                    it.setWeightedDrivePower(
                        drive = 0.0,
                        strafe = 1.0,
                        turn = 0.0
                    )
                } withTimeout(0.2) )
                andThen Command.parallel(
                    OuttakeClaw.release(),
                    OuttakeArm.wallAngle(),
                    OuttakeClaw.wallPitch(),
                    OuttakeClaw.rollDown(),
                    followPath {
                        start(1, -30)
                        curveTo(
                            0, -40,
                            0, -40,
                            48, -65.5,
                            constant(PI / 2)
                        )
                    }
                )
            ))
        )
        (
            hangPreload
            andThen cycle
        ).schedule()

        RunCommand { Drawing.sendPacket() }.schedule()
        RunCommand { println(Drivetrain.position) }.schedule()
        Drivetrain.update()
        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "extendo" ids Extendo::position
            "outtake arm angle" ids { OuttakeArm.angle / PI * 180 }
            "outtake arm setPoint" ids { OuttakeArm.leftMotor.setpoint / PI * 180 }
            "outtake arm effort" ids OuttakeArm.leftMotor::lastWrite
            "" ids CommandScheduler::status
        }

        Telemetry.justUpdate().schedule()
    }


}