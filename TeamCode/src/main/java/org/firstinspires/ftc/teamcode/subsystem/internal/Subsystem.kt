package org.firstinspires.ftc.teamcode.subsystem.internal

import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor

abstract class Subsystem<T : Subsystem<T> >{
    abstract val components: List<Component>

    val motors: ArrayList<Motor>
        get() = with(arrayListOf<Component>()) {
            addAll(components.filter { it is Motor && it !is CRServo } )
            return this as ArrayList<Motor>
        }

    abstract fun update(deltaTime: Double = 0.0)

    open fun reset(){
        components.forEach { it.reset() }
    }

    fun run(function: (T) -> Unit)
        = RunCommand(this) { function(this as T) }

    fun runOnce(function: (T) -> Unit)
        = InstantCommand(this) { function(this as T) }

    fun justUpdate() = (
        run {}
        withName "justUpdate"
        withDescription { (this as T)::class.simpleName!! }
    )

    open fun enable() { }
    open fun disable() { }

    open fun conflictsWith(other: Subsystem<*>): Boolean {
        val output = if (other is SubsystemGroup) other.conflictsWith(this)
        else this == other
        println("$this and $other: $output")
        return output
    }

    abstract class DummySubsystem:Subsystem<DummySubsystem>()
}