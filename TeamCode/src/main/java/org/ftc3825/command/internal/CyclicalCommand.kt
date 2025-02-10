package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class CyclicalCommand(vararg var commands: Command) {
    var currentIndex = 0
        internal set
    val current: Command
        get() = commands[currentIndex]

    fun nextCommand() = Command(
        initialize = {
            currentIndex = (currentIndex + 1) % commands.size
            current.initialize()
        },
        execute = { current.execute() },
        end = { interrupted -> current.end(interrupted) },
        isFinished = { current.isFinished() },
        requirements = current.requirements,
        name = "cyclical command",
        description = { current.description() }
    )


}