package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class RunCommand(vararg requirements: Subsystem<*>, var command: () -> Any): Command(
    execute = command,
    isFinished = { false }
) {
    override var requirements = arrayListOf( *requirements )
}