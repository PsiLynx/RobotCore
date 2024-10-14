package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Motor
import org.ftc3825.command.internal.CommandScheduler

abstract class Subsystem<T : Subsystem<T> >{
    abstract val motors: ArrayList<Motor>

    abstract fun init(hardwareMap: HardwareMap)
    abstract fun update(deltaTime: Double = 0.0)
    fun reset(){
        motors.forEach { it.reset() }
    }

    fun run(function: (T) -> Any): Command {
        return RunCommand(this, command = { function(this as T) } )
    }

    fun runOnce(function: (T) -> Any): Command {
        return InstantCommand(this, command = { function(this as T) })
    }

    fun justUpdate() = RunCommand(this, command = { } )
}
