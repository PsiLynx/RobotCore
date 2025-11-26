package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem

object Gate: Subsystem<Gate>() {
    val servo = HardwareMap.gate()
    override val components = listOf<Component>(servo)

    override fun update(deltaTime: Double) { }

    fun close() = InstantCommand { servo.position = 0.0 }
    fun open()  = InstantCommand { servo.position = 0.2 }
}