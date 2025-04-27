package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.util.control.PIDFGParameters
import kotlin.math.PI

object HWManager {
    val targetLooptimeMS = 20.0
    val components = mutableListOf<Component>()

    fun <T: Component> managed(device: T): T {
        components.add(device)
        return device
    }

    fun crServo(
        name: String,
        direction: Component.Direction,
        ticksPerRev: Double = 1.0,
        wheelRadius: Double = 1 / ( PI * 2 ),
    ) = managed(CRServo(name, direction, ticksPerRev, wheelRadius))

    fun motor(
        name: String,
        rpm: Int,
        direction: Component.Direction = Component.Direction.FORWARD,
        wheelRadius: Double = 1.0,
    ) = managed(Motor(name, rpm, direction, wheelRadius))

    fun pinpoint(name: String) = managed(Pinpoint(name))

    fun servo(name: String, range: Servo.Range) = managed(Servo(name, range))

    fun touchSensor(name: String, default: Boolean = false)
        = managed(TouchSensor(name, default))
}