package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.hang
import org.firstinspires.ftc.teamcode.command.intake
import org.firstinspires.ftc.teamcode.command.cycle
import org.firstinspires.ftc.teamcode.command.rightForTime
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.constant
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.gvf.followPaths
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

@Autonomous(name = " auto")
class Auto: CommandOpMode() {
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
                SampleIntake.pitchBack(),
                WaitCommand(0.1)
            )
            andThen OuttakeClaw.outtakePitch()
            andThen ( OuttakeClaw.grab() parallelTo (
                followPath {
                    start(9, -66)
                    lineTo(0, -26, forward)
                }.withConstraints(9.0)
                racesWith (
                    OuttakeArm.outtakeAngle() until { false }
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
                lineTo(39, -13, forward)
            }
            andThen rightForTime(0.3)
            andThen Command.parallel(
                OuttakeArm.wallAngle() withTimeout 1.8,
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.release(),
                followPath {
                    start(48, -13)
                    lineTo(48, -44, forward)
                    lineTo(48, -13, forward)
                }
            )
            andThen rightForTime(0.3)
            andThen followPath {
                start(52, -13)
                lineTo(52, -42, forward)
                lineTo(52, -13, forward)
            }
            andThen rightForTime(0.42)
            andThen ( followPaths {
                start(62, -13)
                lineTo(62, -54, forward)
                stop()
                curveTo(
                    -7, 0,
                    0, -10,
                    47.5, -66.2, forward
                )
            } withTimeout 3 )
        )
        val hangFirst = hang(
            path {
                start(48, -66)
                lineTo(10, -45, forward)
                lineTo(-7, -25, forward)
                endVel(10.0)
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

	( Extendo.setY(0.03) until { false } ).schedule()

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
