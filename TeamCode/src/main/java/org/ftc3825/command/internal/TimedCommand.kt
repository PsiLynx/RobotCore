package org.ftc3825.command.internal

import org.ftc3825.util.Globals

open class TimedCommand(var seconds: Number, var command: Command) : Command(
    execute = command::execute,
    end = command::end,
    name = "TimedCommand"
) {
    constructor(seconds: Number, command: () -> Unit): this(seconds, RunCommand { command() } )

    var start = 0L
    override fun initialize(){
        start = System.nanoTime()
        command.initialize()
    }

    override fun isFinished() = (
               (System.nanoTime() - start) > ( seconds.toDouble() * 1e9 )
            || command.isFinished()
    )
}
