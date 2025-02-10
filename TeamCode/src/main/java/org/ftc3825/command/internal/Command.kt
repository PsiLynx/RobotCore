package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

open class Command(
    private var initialize: () -> Unit = {},
    private var execute: () -> Unit = {},
    private var end: (interrupted: Boolean) -> Unit = {},
    private var isFinished: () -> Boolean = {false},
    open val requirements: MutableSet<Subsystem<*>> = mutableSetOf(),
    open var name: String = this::class.simpleName.toString(),
    open var description: () -> String = {
        requirements.map { it::class.simpleName!! }.toString()
    }

) {
    fun addRequirement(requirement: Subsystem<*>) {
        requirements.add(requirement)
    }

    open fun initialize() = initialize.invoke()
    open fun execute() = execute.invoke()
    open fun end(interrupted:Boolean) = end.invoke(interrupted)
    open fun isFinished() = isFinished.invoke()


    infix fun andThen(next: Command) = CommandGroup(this, next)
    infix fun withTimeout(seconds: Number) = TimedCommand(seconds, this)
    infix fun racesWith(other: Command) = Command(
        {this.initialize(); other.initialize()},
        {this.execute(); other.execute()},
        {interrupted -> this.end(interrupted); other.end(interrupted)},
        {this.isFinished() or other.isFinished()},
        requirements = (
            this.requirements.toList()
            + other.requirements.toList()
        ).toMutableSet()
    )
    infix fun parallelTo(other: Command) = ParallelCommandGroup(this, other)


    infix fun withInit(function: () -> Unit) = copy(initialize = function)
    infix fun withExecute(function: () -> Unit) = copy(execute = function)
    infix fun withEnd(function: (Boolean) -> Unit) = copy(end = function)
    infix fun until(function: () -> Boolean) = copy(isFinished = function)
    infix fun withName(name: String) = copy(name = name)
    infix fun withDescription(description: () -> String) = copy(
        description = description
    )


    fun schedule() = CommandScheduler.schedule(this)

    override fun toString() = "$name ${description()}"

    fun copy(
        initialize: () -> Unit = this.initialize,
        execute: () -> Unit = this.execute,
        end: (Boolean) -> Unit = this.end,
        isFinished: () -> Boolean = this.isFinished,
        requirements: MutableSet<Subsystem<*>> = this.requirements,
        name: String = this.name,
        description: () -> String = this.description
    ) = Command(
        initialize   = initialize,
        execute      = execute,
        end          = end,
        isFinished   = isFinished,
        requirements = requirements,
        name         = name,
        description  = description
    )

    companion object {
        fun parallel(vararg other: Command) = ParallelCommandGroup(*other)
    }

}
