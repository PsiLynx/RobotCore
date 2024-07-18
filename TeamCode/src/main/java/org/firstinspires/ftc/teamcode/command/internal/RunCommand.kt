package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

class RunCommand(vararg requirements: Subsystem, var command: () -> Any): Command(
    execute = command,
    isFinished = { false }
) {
    override var requirements = arrayListOf( *requirements )
}