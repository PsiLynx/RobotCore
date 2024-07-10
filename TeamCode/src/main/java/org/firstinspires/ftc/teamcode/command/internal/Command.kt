package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

open class Command(
    private var initialize: () -> Any = {},
    private var execute: () -> Any = {},
    private var end: (interrupted: Boolean) -> Any = {},
    private var isFinished: () -> Boolean = {false}

) {
    var requirements:ArrayList<Subsystem> = arrayListOf()
    var readOnly:ArrayList<Subsystem> = arrayListOf()

    fun addRequirement(requirement: Subsystem, write: Boolean=true) {
        if(write){
            this.requirements.add(requirement)
        }
        else{
            this.readOnly.add(requirement)
        }
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
        {this.isFinished() or other.isFinished()}
    )
    infix fun parallelTo(other: Command) = Command(
        {this.initialize(); other.initialize()},
        {
            this.execute()
            other.execute()
            if(this.isFinished()) this.end(false)
            if(other.isFinished()) other.end

        },
        {interrupted -> this.end(interrupted); other.end(interrupted)},
        {this.isFinished() and other.isFinished()}
    )

    infix fun withInit(function: () -> Unit): Command {
        return Command(
            initialize=function
        )
    }
    infix fun withExecute(function: () -> Unit): Command {
        return Command(
            execute=function
        )
    }
    infix fun withEnd(function: (Boolean) -> Unit): Command {
        return Command(
            end=function
        )
    }
    infix fun withIsFinished(function: () -> Boolean): Command {
        return Command(
            isFinished=function
        )
    }

}