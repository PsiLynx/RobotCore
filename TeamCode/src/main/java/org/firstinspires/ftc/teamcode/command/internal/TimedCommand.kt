package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.util.Globals

open class TimedCommand(var seconds: Number, var command: Command): Command() {

    constructor(seconds: Number, command: () -> Any): this(seconds, RunCommand(command=command))

    override fun initialize() {
        command.initialize()
    }

    var start = 0.0
    override fun execute(){
        if(start == 0.0){
            start = Globals.timeSinceStart
        }
        command.execute()
    }

    override fun end(interrupted: Boolean) {
        command.end(interrupted)
    }

    override fun isFinished() = Globals.timeSinceStart - start > seconds.toDouble() || command.isFinished()
}