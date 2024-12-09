package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

class StartEndCommand(
    vararg requirements: Subsystem<*>,
    start: () -> Unit,
    end: () -> Unit):
    Command(
        initialize = start,
        end = {_ -> end()},
        name = "StartEndCommand"
    )
{
        init{
            requirements.map{ addRequirement(it) }
        }
    }
