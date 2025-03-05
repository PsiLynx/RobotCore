package org.teamcode.command

import org.teamcode.command.internal.Command
import org.teamcode.command.internal.WaitCommand
import org.teamcode.subsystem.Extendo
import org.teamcode.subsystem.ExtendoConf
import org.teamcode.subsystem.OuttakeArm
import org.teamcode.subsystem.OuttakeClaw
import org.teamcode.subsystem.SampleIntake
import org.teamcode.util.geometry.Vector2D

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
