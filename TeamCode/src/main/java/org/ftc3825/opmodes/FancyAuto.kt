package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
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
import org.ftc3825.util.geometry.Pose2D
import kotlin.math.PI

@Autonomous(name = "fancy auto")
class FancyAuto: CommandOpMode() {
    override fun initialize() {
        arrayListOf(
            Extendo, Telemetry, Drivetrain,
            OuttakeArm, OuttakeClaw, SampleIntake
        ).forEach { it.reset() }

        val robotStart = Pose2D(9, -66, PI / 2)
        Drivetrain.position = robotStart

        val hangPreload = (
            Command.parallel(
                OuttakeClaw.grab(),
                OuttakeClaw.rollUp(),
                SampleIntake.release(),
                SampleIntake.rollCenter(),
                SampleIntake.pitchDown(),
                WaitCommand(0.1)
            ) andThen OuttakeClaw.outtakePitch()
            andThen ( followPath {
                start(robotStart.vector)
                lineTo(2, -30, constant(PI / 2))
            } parallelTo OuttakeArm.outtakeAngle() withTimeout(1.5) )
            andThen ( OuttakeArm.runToPosition(degrees(140)) withTimeout(0.3) )
            andThen OuttakeClaw.release()
            andThen WaitCommand(0.3)
            andThen (
                Drivetrain.run {
                    it.setWeightedDrivePower(
                        drive = -1.0,
                        strafe = 0.0,
                        turn = 0.0
                    )
                }
                withTimeout(0.4)
                withEnd { Drivetrain.setWeightedDrivePower() }
            )
            andThen ( followPath {
                start(3, -35)
                curveTo(
                    0, -20,
                    0, 40,
                    39, -13,
                    constant(PI / 2)
                )
            } parallelTo ( OuttakeArm.wallAngle() withTimeout(3) ) )
            andThen Command.parallel(
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.release()
            )
            andThen followPath {
                start(39, -13)
                lineTo(41, -13, constant(PI / 2)) // behind first
                lineTo(46, -60, constant(PI / 2)) // push
            }
            andThen followPath {
                start(46, -60)
                lineTo(46, -13, constant(PI / 2)) // behind first
            }
            andThen followPath {
                start(46, -13)
                lineTo(51, -13, constant(PI / 2))
                lineTo(56, -60, constant(PI / 2))
            }
            andThen ( followPath {
                start(56, -60)
                lineTo(48, -45, constant(PI / 2))
            } withTimeout(2) )
            andThen (
                Drivetrain.run {
                    it.setWeightedDrivePower(
                        drive = -0.25
                    )
                }
                withTimeout(2)
                withEnd { Drivetrain.setWeightedDrivePower() }
            )
        )
        fun cycle() = (
            OuttakeClaw.grab()
                andThen WaitCommand(0.5)
                andThen Command.parallel(
                OuttakeClaw.outtakePitch(),
                OuttakeArm.outtakeAngle(),
                WaitCommand(0.15) andThen OuttakeClaw.rollUp(),
            )
            andThen ( followPath {
                start(48, -66)
                lineTo(1, -45, constant(PI / 2))
                lineTo(1, -30, constant(PI / 2))
            } withTimeout(3) )
//            andThen ( Drivetrain.run {
//                it.setWeightedDrivePower(
//                    drive = 0.0,
//                    strafe = 1.0,
//                    turn = 0.0
//                )
//            } withTimeout(0.2) withEnd {
//                Drivetrain.setWeightedDrivePower()
//            } )
            andThen (
            OuttakeArm.runToPosition(degrees(140))
                withTimeout (0.5)
            )
            andThen OuttakeClaw.release()
            andThen WaitCommand(0.3)
            andThen ( Drivetrain.run {
                it.setWeightedDrivePower(
                    drive = -1.0,
                    strafe = 0.0,
                    turn = 0.0
                )
            } withTimeout(0.2)
            withEnd { Drivetrain.setWeightedDrivePower() }
            )
            andThen Command.parallel(
                OuttakeClaw.release(),
                OuttakeArm.wallAngle(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.rollDown(),
                followPath {
                    start(1, -30)
                    curveTo(
                        0, -20,
                        0, -20,
                        48, -60,
                        constant(PI / 2)
                    )
            } withTimeout(2) )
            andThen (
                Drivetrain.run { it.setWeightedDrivePower(drive = -0.25) }
                withTimeout(1)
                withEnd { Drivetrain.setWeightedDrivePower() }
            )
        )
        (
            hangPreload
            andThen cycle()
            andThen cycle()
        ).schedule()

        RunCommand { println(Drivetrain.position) }.schedule()
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