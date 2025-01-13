package org.ftc3825.command.internal

open class TimedCommand(var seconds: Number, var command: Command) : Command(
    execute = command::execute,
    end = command::end,
    requirements = command.requirements,
    name = "TimedCommand"
) {
    constructor(seconds: Number, command: () -> Unit): this(seconds, RunCommand { command() } )

    var start = 0L
    override fun initialize(){
        start = System.nanoTime()
        command.initialize()
    }

    override fun execute() {
        command.execute()
        println( (System.nanoTime() - start) / 1e9 )
    }

    override fun isFinished(): Boolean {
        return (
            (System.nanoTime() - start) > (seconds.toDouble() * 1e9)
                || command.isFinished()
        )
    }
}
