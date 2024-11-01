package org.ftc3825.command.internal

import org.ftc3825.subsystem.Subsystem

open class Command(
    private var initialize: () -> Any = {},
    private var execute: () -> Any = {},
    private var end: (interrupted: Boolean) -> Any = {},
    private var isFinished: () -> Boolean = {false},
    open var requirements: ArrayList<Subsystem<*>> = arrayListOf()

) {
    open var readOnly = arrayListOf<Subsystem<*>>()

    fun addRequirement(requirement: Subsystem<*>, write: Boolean=true) {
        if(write){ this.requirements.add(requirement) }
        else     { this.readOnly    .add(requirement) }
    }

    open fun initialize() = initialize.invoke()
    open fun execute() = execute.invoke()
    open fun end(interrupted:Boolean) = end.invoke(interrupted)
    open fun isFinished() = isFinished.invoke()

    infix fun andThen(next: Command) = CommandGroup(this, next)
    infix fun withTimeout(seconds: Number) = TimedCommand(seconds, this)
    infix fun until (event: () -> Boolean) = this.withIsFinished(event)

    infix fun racesWith(other: Command) = Command(
        {this.initialize(); other.initialize()},
        {this.execute(); other.execute()},
        {interrupted -> this.end(interrupted); other.end(interrupted)},
        {this.isFinished() or other.isFinished()},
        requirements = ArrayList(
            this.requirements.toList()
                    + other.requirements.toList()
        )
    )
    infix fun parallelTo(other: Command) = Command(
        {this.initialize(); other.initialize()},
        {
            if(!this.isFinished()) this.execute()
            if(!other.isFinished()) other.execute()

            if(this.isFinished()) this.end(false)
            if(other.isFinished()) other.end(false)

        },
        {interrupted -> this.end(interrupted); other.end(interrupted)},
        {this.isFinished() && other.isFinished()},
        requirements = ArrayList(
            this.requirements.toList()
                    + other.requirements.toList()
        )
    )

    infix fun withInit(function: () -> Unit) = Command(
        initialize=function,
        execute,
        end,
        isFinished,
        this.requirements
    )

    infix fun withExecute(function: () -> Unit) = Command(
        initialize,
        execute=function,
        end,
        isFinished,
        this.requirements
    )
    infix fun withEnd(function: (Boolean) -> Unit) = Command(
        initialize,
        execute,
        end=function,
        isFinished,
        this.requirements
    )
    infix fun withIsFinished(function: () -> Boolean) = Command(
        initialize,
        execute,
        end,
        isFinished=function,
        this.requirements
    )

    fun schedule() = CommandScheduler.schedule(this)

    override fun toString() = "Command"

}
