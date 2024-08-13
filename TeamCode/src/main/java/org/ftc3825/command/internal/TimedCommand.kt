package org.ftc3825.command.internal

import org.ftc3825.util.Globals

open class TimedCommand(var seconds: Number, var command: Command) : Command(
    initialize = command::initialize,
    end = command::end
) {
    constructor(seconds: Number, command: () -> Any): this(seconds, RunCommand(command=command))

    var start = 0.0
    override fun execute(){
        if(start == 0.0){
            start = Globals.timeSinceStart
        }
        command.execute()
    }
    override fun isFinished() = (
               Globals.timeSinceStart - start > seconds.toDouble()
            || command.isFinished()
    )
}