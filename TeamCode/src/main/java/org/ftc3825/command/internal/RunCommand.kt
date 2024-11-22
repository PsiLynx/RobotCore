package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class RunCommand(vararg requirements: Subsystem<*>, var command: () -> Unit): Command(
    execute = command,
    isFinished = { false },
    name = "RunCommand"
) {
    override var requirements = arrayListOf( *requirements )
}