package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem

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