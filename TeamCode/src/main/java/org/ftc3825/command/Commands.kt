package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.followPath
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
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
        Extendo.setPosition(Vector2D(Extendo.xMax/2, 4))
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

val hang = (
    OuttakeArm.outtakeAngle()
    andThen ( OuttakeArm.setPowerCommand(-0.5) withTimeout(0.5) )
    andThen OuttakeClaw.release()
    withName "hang"
)

val transfer = (
    SampleIntake.pitchBack()
    andThen SampleIntake.rollBack()
    andThen OuttakeClaw.release()
    andThen ( Extendo.transferPos() parallelTo OuttakeArm.transferAngle() )
    andThen OuttakeClaw.grab()
    andThen WaitCommand(0.2) //TODO: tune timeout
    andThen SampleIntake.release()
    withName "transfer"
)
