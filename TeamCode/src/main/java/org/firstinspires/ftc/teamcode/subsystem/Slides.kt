package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.util.pid.PIDFGParameters
import org.firstinspires.ftc.teamcode.util.centimeters
import org.firstinspires.ftc.teamcode.util.slideMotorName

object Slides: Subsystem {
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
                    G = 0
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