package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

class InstantCommand(vararg requirements: Subsystem, var command: () -> Any): Command(
    execute = command,
    isFinished = { true }
) {
    init { requirements.forEach { addRequirement(it) } }
}