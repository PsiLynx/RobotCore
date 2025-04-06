package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.constant
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.gvf.path
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

        OuttakeClaw.release().initialize()
        Thread.sleep(500L)

        val pushDownSpec = OuttakeArm.runToPosition(degrees(140)) withTimeout (0.3)

        fun hang(path: Path) = (
            OuttakeClaw.grab()
            andThen WaitCommand(0.2)
            andThen ( Command.parallel(
                OuttakeClaw.outtakePitch(),

                OuttakeArm.outtakeAngle() until { false },

                WaitCommand(0.15) andThen (
                    OuttakeClaw.rollUp()
                    parallelTo FollowPathCommand(path)
                        .withConstraints(posConstraint = 4.0)
                )
            ) withTimeout (2.5) )
            //andThen pushDownSpec
            andThen OuttakeClaw.release()
            //andThen WaitCommand(0.2)
        )

        val hangPreload = (
            Command.parallel(
                OuttakeClaw.grab(),
                OuttakeClaw.rollUp(),
                SampleIntake.release(),
                SampleIntake.rollCenter(),
                SampleIntake.pitchDown(),
                WaitCommand(0.1)
            )
            andThen OuttakeClaw.outtakePitch()
            andThen ( OuttakeClaw.grab() parallelTo (
                followPath {
                    start(9, -66)
                    lineTo(-5, -26, constant(PI / 2))
                }.withConstraints(posConstraint = 7.0)
                racesWith (
                    OuttakeArm.outtakeAngle() until { false }
                )
            ) )
            //andThen pushDownSpec
            andThen OuttakeClaw.release()
            //andThen WaitCommand(0.2)
        )
        val moveFieldSamps = (
            followPath {
                start(-3, -29)
                curveTo(
                    0, -5,
                    0, 30,
                    39, -30,
                    constant(PI / 2)
                )
                lineTo(38, -13, constant(PI / 2))
            }
            andThen Command.parallel(
                OuttakeArm.wallAngle() withTimeout 1.5,
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.release(),
                followPath {
                    start(39, -13)
                    curveTo(
                        11, 0,
                        0, -10,
                        48, -57,
                        constant(PI / 2)
                    )
                }.withConstraints(4.0, 15.0)
            )
            andThen followPath {
                start(48, -55)
                lineTo(48, -13, constant(PI / 2))
            }
            andThen followPath {
                start(48, -13)
                curveTo(
                    11, 0,
                    0, -10,
                    58, -57,
                    constant(PI / 2)
                )
            }.withConstraints(4.0, 15.0)
            andThen followPath {
                start(58, -57)
                lineTo(58, -13, constant(PI / 2))
            }
            andThen (
                Drivetrain.run { it.setWeightedDrivePower(strafe = 1.0) }
                withTimeout 0.15
                withEnd { Drivetrain.setWeightedDrivePower() }
            )
            andThen followPath {
                start(63, -13)
                lineTo(59.5, -57, constant(PI / 2))
            }.withConstraints(4.0, 15.0)
        )
        val hangFirst = (
            followPath {
                start(59.5, -57)
                lineTo(45.2, -66, constant(PI / 2))
            }
            andThen hang(
                path {
                    start(48, -66)
                    lineTo(20, -52, constant(PI / 2))
                    lineTo(-3, -26, constant(PI / 2))
                }
            )
        )
        fun intake() = Command.parallel(

            WaitCommand(0.2)
                    andThen (OuttakeArm.wallAngle() withTimeout 1.8),

            OuttakeClaw.wallPitch(),

            OuttakeClaw.rollDown(),

            followPath {
                start(-3, -28.5)
                lineTo(2, -40, constant(PI / 2))
                lineTo(49, -66, constant(PI / 2))
            } withTimeout (3)
        )

        fun cycle() = (
            hang(
                path {
                    start(48, -66)
                    lineTo(20, -52, constant(PI / 2))
                    lineTo(-3, -26, constant(PI / 2))
                }
            )
            andThen intake()
        )
        (
            hangPreload
            andThen moveFieldSamps
            andThen hangFirst
            andThen intake()
            andThen cycle()
            andThen cycle()
            andThen cycle()
            andThen cycle()
        ).schedule()

        RunCommand { println(CommandScheduler.deltaTime) }.schedule()
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