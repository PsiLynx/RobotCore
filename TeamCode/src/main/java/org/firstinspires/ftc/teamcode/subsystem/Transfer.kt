package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.component.Servo.Range
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.log

object Transfer: Subsystem<Transfer>() {
    val servo = HardwareMap.hardwareMap?.get( com.qualcomm.robotcore.hardware.Servo::class.java, "s6")!!
    val sensor = HardwareMap.kickerSensor(default = true)

    val pressed get() = sensor.pressed

    override val components = listOf<Component>()

    override fun update(deltaTime: Double) {
        log("pos") value servo.position
        log("pressed") value pressed
    }

    fun runToPos(pos: Double) = runOnce {
        servo.position = pos
    }
    fun runToPos(pos: () -> Double) = run {
        servo.position = pos()
    }

    //fun open() = runToPos(0.84)
    //fun close() = runToPos(0.0)

    fun run()  = run     { servo.position = 1.0 }
    fun stop() = runOnce { servo.position = 0.5 }
}
