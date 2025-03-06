package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

class RunCommand(vararg requirements: Subsystem<*>, var command: () -> Unit): Command(
    requirements = requirements.toMutableSet(),
    execute = command,
    isFinished = { false },
    name = { "RunCommand" }
) {
}