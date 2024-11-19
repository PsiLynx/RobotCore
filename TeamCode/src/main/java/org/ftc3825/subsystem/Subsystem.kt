package org.ftc3825.subsystem

import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Motor

abstract class Subsystem<T : Subsystem<T> >{
    abstract val components: ArrayList<Component>

    abstract fun update(deltaTime: Double = 0.0)

    fun reset(){
        components.forEach { it.reset() }
    }

    fun run(function: (T) -> Any) = RunCommand(this) { function(this as T) }

    fun runOnce(function: (T) -> Any) = InstantCommand(this) { function(this as T) }

    fun justUpdate() = RunCommand(this) { } withName "justUpdate"
}
