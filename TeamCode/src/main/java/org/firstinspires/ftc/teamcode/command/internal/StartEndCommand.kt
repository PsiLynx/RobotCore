package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

class StartEndCommand(
    vararg requirements: Subsystem,
    start: () -> Any,
    end: () -> Any):
    Command(
        initialize = start,
        end = {_ -> end}
    )
{
        init{
            requirements.map{ addRequirement(it) }
        }
    }
