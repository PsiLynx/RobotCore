package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.command.internal.Command

open class TimedCommand(var seconds: Number, var command: Command): Command() {

    constructor(seconds: Number, command: () -> Any): this(seconds, RunCommand(command=command))

    override fun initialize() {
        command.initialize()
    }

    var start = 0L
    override fun execute(){
        if(start == 0L){
            start = System.nanoTime()
        }
        command.execute()
    }

    override fun end(interrupted: Boolean) {
        command.end(interrupted)
    }

    override fun isFinished() = System.nanoTime() - start > seconds.toDouble() * 1E9 || command.isFinished()
}