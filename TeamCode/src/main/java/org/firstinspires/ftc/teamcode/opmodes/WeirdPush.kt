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

@Autonomous(name = " weird push")
class WeirdPush: CommandOpMode() {
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
            andThen (
                OuttakeClaw.outtakePitch()
                parallelTo (
                    OuttakeArm.outtakeAngle() until { false }
                    racesWith (
                        WaitCommand(0.15) andThen (
                            OuttakeClaw.rollUp()
                            parallelTo FollowPathCommand(path)
                                .withConstraints(5.0, 8.0)
                        )
                    )
                    withTimeout (2.5)
                )
            )
            andThen OuttakeClaw.release()
            andThen WaitCommand(0.05)
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
                start(-5, -29)
                curveTo(
                    0, -5,
                    0, 30,
                    39, -30,
                    constant(PI / 2)
                )
                lineTo(39, -11, constant(PI / 2))
            }
            andThen followPath {
                start(39, -11)
                lineTo(61, -11, constant(PI / 2))
            }
            andThen Command.parallel(
                OuttakeArm.wallAngle() withTimeout 1.5,
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.release(),
                followPath {
                    start(59, -11)
                    lineTo(59.4, -57, constant(PI / 2))
                }.withConstraints(4.0, 15.0)
            )
            andThen followPath {
                start(59.4, -57)
                lineTo(59.4, -14, constant(PI / 2))
            }
            andThen (
                Drivetrain.run { it.setWeightedDrivePower(strafe = 0.8, comp = true) }
                withTimeout 0.12
                withEnd { Drivetrain.setWeightedDrivePower() }
            )
            andThen followPath {
                start(61, -14)
                lineTo(61, -57, constant(PI / 2))
            }.withConstraints(4.0, 15.0)
            andThen followPath {
                start(61, -57)
                lineTo(61, -14, constant(PI / 2))
            }
            andThen followPath {
                start(60, -14)
                lineTo(42, -14, constant(PI / 2))
            }
            andThen followPath {
                start(45, -14)
                lineTo(45, -25, constant(PI / 2))
                start(48, -30)
                lineTo(48, -66.2, constant(PI / 2))
            }.withConstraints(velConstraint = 1.0)
        )
        val hangFirst = hang(
            path {
                start(48, -66.2)
                lineTo(20, -52, constant(PI / 2))
                lineTo(-7, -28, constant(PI / 2))
            }
        )
        fun intake() = (
            followPath {
                start(-4, -28.5)
                lineTo(-4, -32, constant(PI / 2))
                lineTo(38, -66, constant(PI / 2))
            } withTimeout (2.5) parallelTo (
                WaitCommand(0.2)
                andThen Command.parallel(
                    OuttakeArm.wallAngle() withTimeout 1.8,
                    OuttakeClaw.wallPitch(),
                    OuttakeClaw.rollDown(),
                )
            )
        )

        fun cycle() = (
            hang(
                path {
                    start(38, -66)
                    lineTo(-7, -28, constant(PI / 2))
                    endVel(4.0)
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