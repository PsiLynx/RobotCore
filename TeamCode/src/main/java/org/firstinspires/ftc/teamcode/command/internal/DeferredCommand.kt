package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem

/**
 * defers constructing a command until initialization.
 */
class DeferredCommand(vararg requirements: Subsystem<*, *>,  var command: () -> Command): Command() {
    var constructedCommand: Command? = null
    override val requirements = requirements.toMutableSet()
    //    get() = constructedCommand!!.requirements
    override fun initialize() {
        constructedCommand = command()
        if (constructedCommand?.requirements != this.requirements) {
            println("DeferredCommand: ${this.name} requirements differ from internal command ")
            requirements.union(constructedCommand?.requirements ?: mutableSetOf())
        }
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