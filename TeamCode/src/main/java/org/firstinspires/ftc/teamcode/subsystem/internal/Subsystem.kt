package org.firstinspires.ftc.teamcode.subsystem.internal

import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand

abstract class Subsystem<T : Subsystem<T> >{

    abstract val io: SubsystemIo

    abstract fun update(deltaTime: Double = 0.0)

    open fun reset() { }

    fun run(function: (T) -> Unit)
        = RunCommand(this) { function(this as T) }

    fun runOnce(function: (T) -> Unit)
        = InstantCommand(this) { function(this as T) }

    fun justUpdate() = (
        run {}
        withName "justUpdate"
        withDescription { (this as T)::class.simpleName!! }
    )

    open fun conflictsWith(other: Subsystem<*>): Boolean {
        val output = if (other is SubsystemGroup) other.conflictsWith(this)
        else this == other
        println("$this and $other: $output")
        return output
    }

    abstract class DummySubsystem:Subsystem<DummySubsystem>()
}