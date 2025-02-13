package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.followPath
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.ExtendoConf
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
import org.ftc3825.util.degrees
import org.ftc3825.util.geometry.Vector2D
import kotlin.math.PI

val intakeClipsStart = Vector2D(63, -54)
val intakeClipsEnd = Vector2D(63, -66)

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

val intakeSpecimen = (
    OuttakeClaw.grab()
    andThen WaitCommand(0.5)
    andThen Command.parallel(
        OuttakeClaw.outtakePitch(),
        OuttakeArm.outtakeAngle(),
        OuttakeClaw.release(),
        WaitCommand(0.15) andThen OuttakeClaw.rollUp(),
    )
)
val hangSpecimen = (
    (OuttakeArm.runToPosition(degrees(150)) withTimeout (0.5))
        andThen OuttakeClaw.release()
        andThen WaitCommand(0.3)
        andThen Command.parallel(
        OuttakeClaw.release(),
        OuttakeClaw.rollDown(),
        OuttakeClaw.wallPitch(),
        OuttakeArm.wallAngle()
    )
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
