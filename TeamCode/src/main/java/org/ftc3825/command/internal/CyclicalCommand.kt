package org.ftc3825.command.internal

class CyclicalCommand(vararg var commands: Command): Command() {
    var currentIndex = 0
        internal set
    val current: Command
        get() = commands[currentIndex]
    override val requirements =
        commands.flatMap { it.requirements }.toMutableSet()

    override fun initialize() {
        commands[0].initialize()
    }

    override fun execute() {
        if(!current.isFinished()) current.execute()
        else current.end(false)
    }

    override fun end(interrupted: Boolean) = current.end(true)

    fun nextCommand() = InstantCommand {
        currentIndex = ( currentIndex + 1 ) % commands.size
    }
}