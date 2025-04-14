package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.gvf.GVFConstants
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Extendo
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.SampleIntake
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import kotlin.math.PI

val intakeClipsStart = Vector2D(63, -54)
val intakeClipsEnd = Vector2D(63, -66)

fun hang(path: Path) = (
    OuttakeClaw.grab()
    andThen WaitCommand(0.2)
    andThen InstantCommand { println(Drivetrain.position) }
    andThen (
        ( WaitCommand(0.4) andThen OuttakeClaw.outtakePitch() )
        parallelTo (
            OuttakeArm.outtakeAngle() until { false }
            racesWith (
                ( WaitCommand(0.4) andThen OuttakeClaw.rollUp() )
                parallelTo ( FollowPathCommand(path).withConstraints(5.0, 8.0) )
                withTimeout (2.4)
            )
	    )
    )
    andThen OuttakeClaw.release()
    andThen WaitCommand(0.1)
)
fun intake(
    path: Path = path {
        start(-4, -29)
        lineTo(40, -66.2, forward)
    }
) = (
    InstantCommand { GVFConstants.DRIVE_P = 0.08 }
    andThen FollowPathCommand(path) withTimeout (2.5) parallelTo (
        WaitCommand(0.2)
        andThen ( OuttakeArm.runToPosition(4 * PI / 5) withTimeout 0.3 )
        andThen Command.parallel(
            OuttakeArm.wallAngle() withTimeout 1.8,
            OuttakeClaw.wallPitch(),
            OuttakeClaw.rollDown(),
        )
    )
    andThen InstantCommand { GVFConstants.DRIVE_P = 0.09 }
)

fun cycle() = (
    hang(
        path {
            start(40, -66)
            lineTo(10, -45, forward)
            lineTo(-7, -25, forward)
            endVel(10.0)
        }
    ) andThen intake()
)

fun rightForTime(time: Double) = (
    Drivetrain.run {
        it.setWeightedDrivePower(strafe = 0.8, comp = true)
    }
    withTimeout time
    withEnd { Drivetrain.setWeightedDrivePower() }
)

val intakeSample = (
    Extendo.extend() until { Extendo.samples.isNotEmpty() }
    andThen Extendo.centerOnSample()
    andThen SampleIntake.pitchDown()
    andThen SampleIntake.run { it.setAngle(Extendo.closestSample.heading) }
    andThen WaitCommand(0.3)
    andThen SampleIntake.grab()
    andThen (
        Extendo.setPosition(Vector2D(ExtendoConf.transferX, 4))
        racesWith (
            SampleIntake.pitchBack()
            andThen SampleIntake.rollCenter()
            andThen SampleIntake.looselyHold()
            andThen WaitCommand(2)
            andThen SampleIntake.grab()
            andThen SampleIntake.pitchDown()
        )
    )
    withName "intake sample"
)

val transfer = (
    Command.parallel(
        SampleIntake.pitchBack(),
        SampleIntake.rollBack(),
        OuttakeClaw.release(),
        Extendo.transferPos() parallelTo OuttakeArm.transferAngle(),
    )
    andThen OuttakeClaw.grab()
    andThen WaitCommand(0.2) //TODO: tune timeout
    andThen SampleIntake.release()
    withName "transfer"
)
