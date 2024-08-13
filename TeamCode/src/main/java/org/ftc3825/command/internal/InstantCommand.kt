package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class InstantCommand(vararg requirements: Subsystem, var command: () -> Any): Command(
    execute = command,
    isFinished = { true }
) {
    override var requirements = arrayListOf( *requirements )
}