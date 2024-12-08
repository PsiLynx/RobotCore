package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class InstantCommand(vararg requirements: Subsystem<*>, var command: () -> Unit): Command(
    initialize = command,
    isFinished = { true },
    name = "InstantCommand"
) {
    override var requirements = arrayListOf( *requirements )
}
