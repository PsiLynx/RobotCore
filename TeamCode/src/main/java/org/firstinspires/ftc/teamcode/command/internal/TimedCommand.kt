package org.firstinspires.ftc.teamcode.command.internal

open class TimedCommand(var seconds: Number, var command: Command) : Command(
    requirements = command.requirements,
    name = { "TimedCommand" }
) {
    constructor(seconds: Number, command: () -> Unit): this(seconds, RunCommand { command() } )

    var start = 0L
    override fun initialize(){
        start = System.nanoTime()
        command.initialize()
    }

    override fun execute() = command.execute()

    override fun end(interrupted: Boolean) = command.end(interrupted)

    override fun isFinished() = (
               (System.nanoTime() - start) > ( seconds.toDouble() * 1e9 )
            || command.isFinished()
    )
}
