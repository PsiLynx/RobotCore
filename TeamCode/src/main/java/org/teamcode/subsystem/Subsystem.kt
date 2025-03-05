package org.teamcode.subsystem

import org.teamcode.command.internal.InstantCommand
import org.teamcode.command.internal.RunCommand
import org.teamcode.component.Component
import org.teamcode.component.Motor

interface Subsystem<T : Subsystem<T> >{
    val components: ArrayList<Component>

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

    abstract class DummySubsystem:Subsystem<DummySubsystem>
}
