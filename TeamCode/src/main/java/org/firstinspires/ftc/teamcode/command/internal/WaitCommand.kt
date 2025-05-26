package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.util.Globals

open class WaitCommand(var seconds: Number): Command(
    name = { "WaitCommand" }
) {

    var start = Globals.currentTime
    override fun initialize(){
        start = Globals.currentTime
    }

    override fun isFinished() = Globals.currentTime - start > seconds.toDouble()
}
