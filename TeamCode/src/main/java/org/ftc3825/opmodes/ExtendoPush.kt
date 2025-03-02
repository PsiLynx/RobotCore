package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
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
import org.ftc3825.util.geometry.Vector2D
import kotlin.math.PI

@Autonomous(name = "extendo push")
@Disabled
class ExtendoPush: CommandOpMode() {
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
                Drivetrain.run { it.setWeightedDrivePower(drive = -1.0) }
                withTimeout(0.2)
            )
            andThen (
                Drivetrain.run { it.setWeightedDrivePower(turn = -1.0) }
                withTimeout(0.6)
                withEnd {
                    Drivetrain.setWeightedDrivePower()
                }
            )
        )
        val moveFieldSpecimens = (
            Command.parallel(
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeClaw.release()
            )
            andThen ( ( followPath {
                start(3, -45)
                lineTo(30, -40, constant(-PI / 2))
                lineTo(39, -13, constant(-PI / 2))
            } parallelTo OuttakeArm.wallAngle() ) withTimeout(2.5) )
            andThen ( followPath {
                start(39, -13)
                lineTo(47, -18, constant(- PI / 2)) // behind first
            } withTimeout(1) )
            andThen SampleIntake.grab()
            andThen WaitCommand(0.3)
            andThen ( SampleIntake.pitchBack() parallelTo SampleIntake.rollLeft() )
            andThen WaitCommand(0.3)
            andThen (
                Drivetrain.run {
                    it.setWeightedDrivePower(drive = 0.5)
                }
                parallelTo Extendo.setPowerCommand(0.0, 1.0)
                until { Drivetrain.position.y < -33 }
                withEnd {
                    Drivetrain.setWeightedDrivePower()
                    Extendo.setPower(Vector2D())
                }
            )
            andThen SampleIntake.release()
            andThen WaitCommand(0.2)
            andThen SampleIntake.grab()
            andThen WaitCommand(0.2)
            andThen ( (followPath {
                start(47, -35)
                lineTo(47, -13, constant(- PI / 2))
            } parallelTo Extendo.retract() ) withTimeout(1) ) // finish 1
            andThen Command.parallel(
                SampleIntake.release(),
                SampleIntake.rollCenter(),
                SampleIntake.pitchDown()
            )
            andThen ( followPath {
                start(47, -13)
                lineTo(57, -19, constant(- PI / 2)) // behind first
            } withTimeout(1) )
            andThen SampleIntake.grab()
            andThen WaitCommand(0.3)
            andThen ( SampleIntake.pitchBack() parallelTo SampleIntake.rollLeft() )
            andThen WaitCommand(0.3)
            andThen (
                Drivetrain.run {
                    it.setWeightedDrivePower(drive = 0.5, strafe = 0.0, turn = 0.0)
                }
                parallelTo Extendo.setPowerCommand(0.0, 1.0)
                until { Drivetrain.position.y < -33 }
                withEnd {
                    Drivetrain.setWeightedDrivePower()
                    Extendo.setPower(Vector2D())
                }
            )
            andThen SampleIntake.release()
            andThen WaitCommand(0.2)
            andThen SampleIntake.grab()
            andThen WaitCommand(0.2)
            andThen Extendo.retract() // finish 2
            andThen ( followPath {
                start(57, -35)
                lineTo(48, -66, constant(PI / 2))
            } withTimeout(1) )
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
                    lineTo(48, -55, constant(PI / 2))
                    lineTo(48, -65.5, constant(PI / 2))
                } withTimeout(3) )
            )
        (
            hangPreload
            andThen moveFieldSpecimens
            andThen cycle()
            andThen cycle()
            andThen cycle()
        ).schedule()

        RunCommand { println(Drivetrain.position) }.schedule()
        Drivetrain.update()
        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "extendo" ids Extendo::position
            "vel" ids Drivetrain::velocity
            "outtake arm angle" ids { OuttakeArm.angle / PI * 180 }
            "outtake arm setPoint" ids { OuttakeArm.leftMotor.setpoint / PI * 180 }
            "outtake arm effort" ids OuttakeArm.leftMotor::lastWrite
            "" ids CommandScheduler::status
        }

        Telemetry.justUpdate().schedule()
    }


}