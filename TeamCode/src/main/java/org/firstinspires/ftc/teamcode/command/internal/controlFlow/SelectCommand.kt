package org.firstinspires.ftc.teamcode.command.internal.controlFlow

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import java.util.function.Supplier

open class SelectCommand<T>(
    var supplier: Supplier<T>,
    var builder: SelectCommand<T>.() -> Unit
): Command() {
    protected val commands = mutableMapOf<T, Command>()
    private var defaultCommand: Command = InstantCommand { }
    private var selected = defaultCommand

    init {
        this.apply(builder)
        commands.forEach { (_, command) ->
            command.requirements.forEach { addRequirement(it) }
        }
    }

    override fun initialize() {
        val supplierValue = supplier.get()
        selected = commands[supplierValue] ?: defaultCommand
        selected.initialize()
    }

    infix fun T.runs(command: Command){
        commands[this] = command
    }
    fun default(command: Command){
        defaultCommand = command
    }

    override fun execute() { selected.execute() }
    override fun end(interrupted: Boolean) { selected.end(interrupted) }
    override fun isFinished(): Boolean { return selected.isFinished() }

    override var description: () -> String
        get() = selected.description
        set(value) {selected.description = value }

    override var name: () -> String
        get() = selected.name
        set(value) {selected.name = value }

}