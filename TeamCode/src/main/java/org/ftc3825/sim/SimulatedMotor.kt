package org.ftc3825.sim

import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.util.Globals
import kotlin.math.abs


class SimulatedMotor(): FakeMotor() {
    private var position = 0.0
    private var velocity = 0.0
    private var acceleration = 0.0

    private var voltage = 0.0



    override fun update(deltaTime: Double) {
        val a = 416.38
        val b = -0.99209
        acceleration = a * voltage + b * velocity
        velocity += acceleration * deltaTime
        position += velocity * deltaTime
    }

    override fun getPower() = voltage / Globals.robotVoltage
    override fun setPower(p0: Double) { voltage = p0.coerceIn(-1.0, 1.0) * Globals.robotVoltage }

    override fun getCurrentPosition() = position.toInt()
    override fun setCurrentPosition(newPos:Number){ position = newPos.toDouble() }

}