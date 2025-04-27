package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor

interface Subsystem<T : Subsystem<T> >{
    val components: List<Component>

    val motors: ArrayList<Motor>
        get() = with(arrayListOf<Motor>()) {
            addAll(components.filterIsInstance<Motor>())
            return this
        }

    fun update(deltaTime: Double = 0.0)

    fun reset(){
        components.forEach { it.reset() }
    }

    fun run(function: (T) -> Unit) = RunCommand(this) { function(this as T) }

    fun runOnce(function: (T) -> Unit) = InstantCommand(this) { function(this as T) }

    fun justUpdate() = RunCommand(this) { } withName "justUpdate" withDescription { (this as T)::class.simpleName!! }

    fun conflictsWith(other: Subsystem<*>): Boolean {
        val output = if (other is SubsystemGroup) other.conflictsWith(this)
        else this == other
        println("${this} and ${other}: $output")
        return output
    }

    abstract class DummySubsystem:Subsystem<DummySubsystem>
}
