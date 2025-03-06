package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.subsystem.Extendo
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.SampleIntake
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D

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
