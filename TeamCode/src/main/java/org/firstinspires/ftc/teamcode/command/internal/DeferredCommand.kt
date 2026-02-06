package org.firstinspires.ftc.teamcode.command.internal

/**
 * defers constructing a command until initialization.
 */
class DeferredCommand(val command: () -> Command): Command() {
    var constructedCommand: Command? = null
    override fun initialize() {
        constructedCommand = command()
        constructedCommand!!.initialize()
    }

    override fun execute() {
        constructedCommand!!.execute()
    }
    override fun end(interupted: Boolean) {
        constructedCommand!!.end(interupted)
        constructedCommand = null
    }

    override fun isFinished(): Boolean {
        return constructedCommand!!.isFinished()
    }

    override var name = {
        constructedCommand?.name() ?: "Deffered (constructing)"
    }
    override var description = {
        constructedCommand?.description() ?: ""
    }
}