package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.hang
import org.firstinspires.ftc.teamcode.command.intake
import org.firstinspires.ftc.teamcode.command.cycle
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.gvf.followPaths
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Extendo
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.SampleIntake
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
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

        val hangPreload = (
            Command.parallel(
                OuttakeClaw.grab(),
                OuttakeClaw.rollUp(),
                SampleIntake.release(),
                SampleIntake.rollCenter(),
                SampleIntake.pitchDown(),
                WaitCommand(0.1)
            )
            andThen OuttakeClaw.ramPitch()
            andThen ( OuttakeClaw.grab() parallelTo (
                followPath {
                    start(9, -66)
                    lineTo(-5, -26, forward)
                }.withConstraints(posConstraint = 7.0)
                racesWith (
                    OuttakeArm.ramAngle() until { false }
                )
            ) )
            //andThen pushDownSpec
            andThen OuttakeClaw.release()
            //andThen WaitCommand(0.2)
        )
        val moveFieldSamps = (
            followPaths {
                start(-5, -29)
                curveTo(
                    0, -5,
                    0, 30,
                    39, -30,
                    forward
                )
                lineTo(39, -11, forward)

                stop()
                lineTo(61, -11, forward)
            }
            andThen Command.parallel(
                OuttakeArm.wallAngle() withTimeout 1.5,
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.release(),
                followPath {
                    start(59, -11)
                    lineTo(59.4, -57, forward)
                }.withConstraints(4.0, 15.0)
            )
            andThen followPath {
                start(59.4, -57)
                lineTo(59.4, -14, forward)
            }
            andThen (
                Drivetrain.run { it.setWeightedDrivePower(strafe = 0.8, comp = true) }
                withTimeout 0.12
                withEnd { Drivetrain.setWeightedDrivePower() }
            )
            andThen followPaths {
                start(61, -14)
                lineTo(61, -57, forward)

                stop(4.0, 15.0)
                lineTo(61, -14, forward)

                stop()
                start(60, -14)
                lineTo(42, -14, forward)

                stop()
                start(45, -14)
                lineTo(45, -25, forward)
                start(48, -30)
                lineTo(48, -66.2, forward)
                stop(velConstraint = 1.0)
            }
        )
        val hangFirst = hang(
            path {
                start(48, -66.2)
                lineTo(20, -52, forward)
                lineTo(-7, -28, forward)
            }
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
            "outtake arm setPoint" ids { OuttakeArm.targetPos / PI * 180 }
            "outtake arm effort" ids OuttakeArm.leftMotor::lastWrite
            "" ids CommandScheduler::status
        }

        Telemetry.justUpdate().schedule()
    }


}