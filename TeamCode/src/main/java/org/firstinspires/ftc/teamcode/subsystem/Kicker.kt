package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.log

object Kicker: Subsystem<Kicker>() {
    val servo = HardwareMap.kicker(range = Servo.Range.Default)
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

    fun open() = runToPos(0.74)
    fun close() = runToPos(0.0)

}
