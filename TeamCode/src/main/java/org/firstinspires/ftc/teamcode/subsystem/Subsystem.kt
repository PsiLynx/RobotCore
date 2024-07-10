package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand

interface Subsystem{
    fun init(hardwareMap: HardwareMap): Unit
    fun update(deltaTime: Double = 0.0)

    fun run(function: () -> Any){
        CommandScheduler.schedule(
            RunCommand(this, command = function)
        )
    }

    fun runOnce(function: () -> Any){
        CommandScheduler.schedule(
            InstantCommand(this, command = function)
        )
    }

    var initialized: Boolean
}
