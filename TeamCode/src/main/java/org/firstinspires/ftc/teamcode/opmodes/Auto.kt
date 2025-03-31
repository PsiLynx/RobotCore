package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.constant
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Extendo
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.SampleIntake
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import kotlin.math.PI

@Autonomous(name = "auto")
class Auto: CommandOpMode() {
    override fun initialize() {
        arrayListOf(
            Extendo, Telemetry, Drivetrain,
            OuttakeArm, OuttakeClaw, SampleIntake
        ).forEach { it.reset() }

        Drivetrain.position = Pose2D(9, -66, PI / 2)

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
                start(9, -66)
                lineTo(-1, -30, constant(PI / 2))
            } parallelTo OuttakeArm.outtakeAngle() withTimeout(1.5) )
            andThen ( OuttakeArm.runToPosition(degrees(140)) withTimeout(0.35) )
            andThen OuttakeClaw.release()
            andThen WaitCommand(0.3)
            andThen followPath {
                start(-1, -35)
                curveTo(
                    0, -5,
                    0, 30,
                    37, -30,
                    constant(PI / 2)
                )
                lineTo(37, -13, constant(PI / 2))
            }
            andThen Command.parallel(
                OuttakeArm.wallAngle() withTimeout 1.5,
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.release(),
                followPath {
                    start(39, -13)
//                    lineTo(41, -13, constant(PI / 2)) // behind first
//                    lineTo(48, -46, constant(PI / 2)) // push
                    curveTo(
                        11, 0,
                        0, -10,
                        48, -60,
                        constant(PI / 2)
                    )
                }
            )
            andThen followPath {
                start(48, -60)
                lineTo(48, -13, constant(PI / 2)) // behind first
            }
            andThen followPath {
                start(48, -13)
                //lineTo(51, -13, constant(PI / 2))
                curveTo(
                    11, 0,
                    0, -10,
                    58, -65.4,
                    constant(PI / 2)
                )
            }
        )
        fun cycle() = (
            OuttakeClaw.grab()
                andThen WaitCommand(0.5)
                andThen ( Command.parallel(
                OuttakeClaw.outtakePitch(),
                OuttakeArm.outtakeAngle() ,
                WaitCommand(0.15) andThen OuttakeClaw.rollUp(),
                WaitCommand(0.15) andThen followPath {
                    start(48, -65.2)
                    curveTo(
                        0, 10,
                        -5, 10,
                        -1, -30,
                        constant(PI / 2)
                    )
                }
            ) withTimeout(3) )
            andThen (
                OuttakeArm.runToPosition(degrees(140)) withTimeout (0.35)
            )
            andThen OuttakeClaw.release()
            andThen WaitCommand(0.3)
            andThen Command.parallel(
                WaitCommand(0.5) andThen (
                        OuttakeArm.wallAngle() withTimeout 1.5
                ),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.rollDown(),
                followPath {
                    start(1, -30)
                    curveTo(
                        0, -20,
                        0, -20,
                        48, -55,
                        constant(PI / 2)
                    )
                    lineTo(48, -65.2, constant(PI / 2))
                } withTimeout(3)
            )
        )
        (
            hangPreload
            andThen cycle()
            andThen cycle()
            andThen cycle()
        ).schedule()

        RunCommand { println(Drivetrain.position) }.schedule()
        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "vel" ids Drivetrain::velocity
            "extendo" ids Extendo::position
            "outtake arm angle" ids { OuttakeArm.angle / PI * 180 }
            "outtake arm setPoint" ids { OuttakeArm.leftMotor.setpoint / PI * 180 }
            "outtake arm effort" ids OuttakeArm.leftMotor::lastWrite
            "" ids CommandScheduler::status
        }

        Telemetry.justUpdate().schedule()
    }


}