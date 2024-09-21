package org.ftc3825.util

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.Motor
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.pid.PIDFGParameters

object Slides: Subsystem<Claw> {
    override var initialized = false

    lateinit var motor: Motor

    val position: Double
        get() = motor.position
    val velocity: Double
        get() = motor.velocity

    override val motors
        get() = arrayListOf(motor)

    override fun init(hardwareMap: HardwareMap) {
        if(!initialized) {
            motor = Motor(
                slideMotorName,
                hardwareMap,
                rpm = 435,
                wheelRadius = centimeters(1),
                controllerParameters = PIDFGParameters(
                    P = 0.0003,
                    I = 0.000,
                    D = 0.001,
                    F = 0,
                )
            )
            motor.useInternalEncoder()
        }
        initialized = true
    }

    override fun update(deltaTime: Double) {
        motor.update(deltaTime)
    }

    fun runToPosition(ticks: Number){
        motor.runToPosition(ticks.toDouble())
    }
}