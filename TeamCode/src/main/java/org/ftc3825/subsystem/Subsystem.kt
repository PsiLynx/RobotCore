package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Motor

interface Subsystem{
    var initialized: Boolean

    val motors: ArrayList<Motor>

    fun init(hardwareMap: HardwareMap)
    fun update(deltaTime: Double = 0.0)
    fun reset(){
        motors.forEach { it.reset() }
    }

    fun run(function: (Subsystem) -> Any) =
            RunCommand(this, command = { function(this) } )

    fun runOnce(function: (Subsystem) -> Any) =
            InstantCommand(this, command = { function(this) } )
}
