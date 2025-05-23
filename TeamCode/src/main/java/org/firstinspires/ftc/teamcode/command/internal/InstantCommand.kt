package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

class InstantCommand(vararg requirements: Subsystem<*>, var command: () -> Unit): Command(
    initialize = command,
    name = { "InstantCommand" }
) {
    override var requirements: MutableSet<Subsystem<*>> = mutableSetOf( *requirements )

    final override fun isFinished() = true
    //this cannot be overriden, it will mess up callbacks
}
