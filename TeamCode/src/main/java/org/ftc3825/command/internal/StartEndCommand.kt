package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class StartEndCommand(
    vararg requirements: Subsystem<*>,
    start: () -> Unit,
    end: () -> Unit
): Command(
    initialize = start,
    end = {_ -> end()},
    isFinished = { false },
    name = { "StartEndCommand" }
) {
    constructor(start: Command, end: Command): this(
        requirements =
            (start.requirements + end.requirements).toSet().toTypedArray(),
        start = {
            start.initialize()
            start.execute()
        },
        end = {
            end.initialize()
            end.execute()
        }
    )

    init {
        requirements.map { addRequirement(it) }
    }

}