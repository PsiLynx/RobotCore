package org.teamcode.command.internal

import org.teamcode.component.Component
import org.teamcode.subsystem.Subsystem

class CyclicalCommand(vararg var commands: Command) {
    var currentIndex = 0
        internal set
    val current: Command
        get() = commands[currentIndex]
    object CyclicalSubsystem: Subsystem<Subsystem.DummySubsystem> {
        override val components = arrayListOf<Component>()
        override fun update(deltaTime: Double) { }

    }

    fun nextCommand(): Command {
        return Command(
            initialize = {
                currentIndex = (currentIndex + 1) % commands.size
                current.initialize()
            },
            execute = { current.execute() },
            end = { interrupted -> current.end(interrupted) },
            isFinished = { current.isFinished() },
            requirements = (
                current.requirements
                //+ CyclicalSubsystem
            ).toMutableSet(),
            name = { current.name() },
            description = {
                (
                    current.description()
                    + "|"
                    + current.requirements.joinToString { "$it, " }
                )
            }
        )
    }


}