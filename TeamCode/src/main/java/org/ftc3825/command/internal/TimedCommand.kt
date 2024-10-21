package org.ftc3825.command.internal

import org.ftc3825.util.Globals

open class TimedCommand(var seconds: Number, var command: Command) : Command(
    execute = command::execute,
    end = command::end
) {
    constructor(seconds: Number, command: () -> Any): this(seconds, RunCommand(command=command))

    var start = 0.0
    override fun initialize(){
        start = Globals.timeSinceStart
        command.initialize()
    }

    override fun isFinished() = (
               (Globals.timeSinceStart - start) > seconds.toDouble()
            || command.isFinished()
    )
}
