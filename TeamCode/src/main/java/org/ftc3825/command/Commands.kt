package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.followPath
import org.ftc3825.subsystem.ClipIntake
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
import org.ftc3825.util.Vector2D
import kotlin.math.PI

val intakeClipsStart = Vector2D(63, -54)
val intakeClipsEnd = Vector2D(63, 66)

val intakeClips = (
    Command.parallel(
        ClipIntake.flipBack(),
        ClipIntake.pitchLeft(),
        ClipIntake.release()
    ) andThen followPath {
        start(intakeClipsStart)
        lineTo(intakeClipsEnd, HeadingType.Constant(-PI / 2))
    } andThen ClipIntake.grab()
    withName "intake clips"
)

val goToHumanPlayer = (
    Command.parallel(
        followPath {
            start(9, -30)
            lineTo(intakeClipsStart, HeadingType.Constant(PI / 2))
        },
        OuttakeArm.wallAngle(),
        OuttakeClaw.release(),
        OuttakeClaw.rollCenter(),

    ) andThen ( intakeClips parallelTo OuttakeClaw.grab() )
    withName "go to human player"
)

val intakeSample = (
    Extendo.extend() until { Extendo.samples.isNotEmpty() }
    andThen Extendo.centerOnSample()
    andThen SampleIntake.pitchDown()
    andThen SampleIntake.setAngle(Extendo.samples.minBy { it.mag }.heading)
    andThen WaitCommand(0.3)
    andThen (
        Extendo.setPosition(Vector2D(Extendo.xMax/2, 4))
        racesWith (
            SampleIntake.pitchForward()
            andThen SampleIntake.rollCenter()
            andThen SampleIntake.looslyHold()
            andThen WaitCommand(2)
            andThen SampleIntake.grab()
            andThen SampleIntake.pitchDown()
        )
    )
    andThen Extendo.retract()
    withName "intake sample"
)

val hang = (
    OuttakeArm.clippingAngle()
    andThen ( OuttakeArm.setPower(-0.5) withTimeout(0.5) )
    andThen OuttakeClaw.release()
)

val clip = (
    Extendo.retract()
    andThen SampleIntake.clipPitch()
)
