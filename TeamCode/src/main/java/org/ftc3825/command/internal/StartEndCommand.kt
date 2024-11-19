package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class StartEndCommand(
    vararg requirements: Subsystem<*>,
    start: () -> Any,
    end: () -> Any):
    Command(
        initialize = start,
        end = {_ -> end},
        name = "StartEndCommand"
    )
{
        init{
            requirements.map{ addRequirement(it) }
        }
    }
