package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.Motor

interface Subsystem{
    var initialized: Boolean

    val motors: ArrayList<Motor>

    fun init(hardwareMap: HardwareMap)
    fun update(deltaTime: Double = 0.0)

    fun run(function: (Subsystem) -> Any) =
            RunCommand(this, command = { function(this) } )

    fun runOnce(function: (Subsystem) -> Any) =
            InstantCommand(this, command = { function(this) } )
}
