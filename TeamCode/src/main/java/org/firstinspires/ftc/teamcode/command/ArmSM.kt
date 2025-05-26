package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.degrees

object ArmSM: CyclicalCommand(
    Command.parallel(
        OuttakeClaw.release(),
        OuttakeClaw.rollDown(),
        OuttakeClaw.wallPitch(),
        OuttakeArm.wallAngle() until { false }
    ) withName "intake position",

    OuttakeClaw.grab()
    andThen WaitCommand(OuttakeClawConf.intakeWait)
    andThen Command.parallel(
        OuttakeClaw.outtakePitch(),
        OuttakeArm.outtakeAngle() until { false },
        WaitCommand(0.15) andThen OuttakeClaw.rollUp(),
    ) andThen OuttakeArm.justUpdate() withName "outtake angle",

    OuttakeArm.runToPosition(degrees(180)) withTimeout 0.5
    parallelTo (WaitCommand(0.3) andThen OuttakeClaw.release() )

) {
    var startTime = Globals.currentTime
    var goBack = false

    override fun initialize() {

        println("delta: ${Globals.currentTime - startTime}")

        goBack = (
            currentIndex == 1
            && Globals.currentTime - startTime < 1.0
        )
        startTime = Globals.currentTime
        if (goBack) lastCommand().initialize()
        else nextCommand().initialize()
    }

    override fun execute() = current.execute()
    override fun isFinished() = current.isFinished()
    override val requirements: MutableSet<Subsystem<*>>
        get() = current.requirements

}