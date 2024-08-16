package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.command.LogCommand
import org.ftc3825.command.RunMotorToPower
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.subsystem.Slides

@Disabled
abstract class CommandOpMode: OpMode() {
    init {
        CommandScheduler.init(hardwareMap)
    }

    override fun loop() {
        CommandScheduler.update()
    }

    override fun stop() {
        CommandScheduler.end()
    }
}
