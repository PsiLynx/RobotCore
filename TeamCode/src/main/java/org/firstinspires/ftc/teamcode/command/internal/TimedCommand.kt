package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.util.Globals

open class TimedCommand(var seconds: Number, var command: Command) : Command(
    requirements = command.requirements,
    name = { "" },
    description = { command.toString() }
) {
    constructor(seconds: Number, command: () -> Unit): this(seconds, RunCommand { command() } )

    var start = Globals.currentTime
    override fun initialize(){
        start = Globals.currentTime
        command.initialize()
    }

    override fun execute() = command.execute()

    override fun end(interrupted: Boolean) = command.end(interrupted)

    override fun isFinished() = (
        Globals.currentTime - start > seconds.toDouble()
        || command.isFinished()
    )
}
