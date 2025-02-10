package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class CyclicalCommand(vararg var commands: Command): Command() {
    var currentIndex = 0
        internal set
    val current: Command
        get() = commands[currentIndex]
    override val requirements = mutableSetOf<Subsystem<*>>()

    init {
        commands.forEach { command ->
            command.requirements.forEach { addRequirement(it); println("$it k") }
        }
    }

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
        current.initialize()
    }

    override var name = "cyclical command: [${
        commands.joinToString {
            if(current == it) "$it, " 
            else it.toString().uppercase() + ", "
        }
    }]"

}