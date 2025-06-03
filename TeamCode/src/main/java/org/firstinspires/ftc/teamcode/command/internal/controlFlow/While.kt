package org.firstinspires.ftc.teamcode.command.internal.controlFlow

import org.firstinspires.ftc.teamcode.command.internal.Command

class While(val condition: () -> Boolean, val command: Command): Command() {
    override val requirements = command.requirements
    override var name = { "While Command" }
    override var description = command.description
    override fun initialize() = command.initialize()
    override fun execute() {
        if(condition() == true && command.isFinished()) {
            command.end(interrupted = false)
            command.initialize()
        }
        else if(command.isFinished()) return
        command.execute()
    }
    override fun end(interrupted: Boolean) = command.end(interrupted)
    override fun isFinished() = command.isFinished() && condition() == false
}