package org.ftc3825.util

import org.ftc3825.component.Component
import org.ftc3825.component.Motor
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.pid.PIDFGParameters

object Slides: Subsystem<Slides> {
    val motor = Motor(
        slideMotorName,
        rpm = 435,
        wheelRadius = centimeters(1),
        controllerParameters = PIDFGParameters(
            P = 0.0003,
            I = 0.000,
            D = 0.001,
            F = 0,
        )
    )

    val position: Double
        get() = motor.ticks
    val velocity: Double
        get() = motor.velocity

    override var components = arrayListOf<Component>()

    init {
        motor.useInternalEncoder()

    }

    override fun update(deltaTime: Double) {
        motor.update(deltaTime)
    }

    fun runToPosition(ticks: Number){
        motor.runToPosition(ticks.toDouble())
    }
}
