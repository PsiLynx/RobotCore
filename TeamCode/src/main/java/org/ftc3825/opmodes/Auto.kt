package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.intakeSpecimen
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RepeatCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.gvf.HeadingType.Companion.constant
import org.ftc3825.gvf.HeadingType.Companion.tangent
import org.ftc3825.gvf.HeadingType.Companion.linear
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
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.geometry.Vector2D
import kotlin.math.PI

@Autonomous(name = "auto")
class Auto: CommandOpMode() {
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
                OuttakeClaw.outtakePitch(),
                SampleIntake.release(),
                SampleIntake.rollCenter(),
                SampleIntake.pitchDown(),
                WaitCommand(0.1)
            ) andThen followPath {
                start(robotStart.vector)
                lineTo(12, -32, constant(PI / 2))
            } parallelTo OuttakeArm.outtakeAngle()
            andThen (
                OuttakeArm.runToPosition(degrees(150))
                withTimeout(0.5)
                withEnd { OuttakeArm.setPower(0.0) }
            )
            andThen followPath {
                start(12, -32)
                lineTo(15, -32, constant(PI / 2))
                quickly(OuttakeClaw.release())
                curveTo(
                    0, -10,
                    0, 10,
                    35, -24,
                    constant(PI / 2)
                )
                curveTo(
                    0, 15,
                    0, -15,
                    48, -24,
                    constant(PI / 2)
                )
                lineTo(48, -50, constant(PI / 2))
                lineTo(48, -24, constant(PI / 2))
                curveTo(
                    0, 15,
                    0, -15,
                    58, -24,
                    constant(PI / 2)
                )
                lineTo(58, -50, constant(PI / 2))
                lineTo(48, -66, constant(PI / 2))
            }
        )
        val cycle = (
            RepeatCommand( times = 3, command = (
                intakeSpecimen
                andThen followPath {
                    start(48, -66)
                    lineTo(12, -30, constant(PI / 2))
                }
                andThen (
                    OuttakeArm.runToPosition(degrees(150))
                    withTimeout (0.5)
                )
                andThen followPath {
                    start(12, -30)
                    lineTo(15, -30, constant(PI / 2))
                }
                andThen Command.parallel(
                    OuttakeArm.wallAngle(),
                    OuttakeClaw.wallPitch(),
                    OuttakeClaw.rollDown(),
                    followPath {
                        start(3, -30)
                        curveTo(
                            0, -20,
                            0, -20,
                            48, -66,
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
            "" ids CommandScheduler::status
        }

        Telemetry.justUpdate().schedule()
    }


}