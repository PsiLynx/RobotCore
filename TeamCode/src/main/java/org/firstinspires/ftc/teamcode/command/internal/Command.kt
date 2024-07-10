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

    fun andThen(next: Command) = CommandGroup(this, next)
    fun withTimeout(seconds: Number) = TimedCommand(seconds, this)
    fun raceWith(other: Command) = Command(
        {this.initialize(); other.initialize()},
        {this.execute(); other.execute()},
        {interrupted -> this.end(interrupted); other.end(interrupted)},
        {this.isFinished() or other.isFinished()}
    )
    fun parallelTo(other: Command) = Command(
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
}