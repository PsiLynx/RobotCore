package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.util.pid.PIDFGParameters
import org.firstinspires.ftc.teamcode.util.centimeters

object Slides: Subsystem {
    override var initialized = false

    lateinit var slideMotor: Motor

    override fun init(hardwareMap: HardwareMap) {
        if(!initialized) {
            slideMotor = Motor(
                "slideMotor",
                hardwareMap,
                rpm = 435,
                wheelRadius = centimeters(1),
                controllerParameters = PIDFGParameters(
                    P = 0.00015,
                    I = 0.00003,
                    D = 0.002,
                    F = 0.01,
                    G = 0
                )
            )
            slideMotor.useInternalEncoder()
        }
        initialized = true
    }

    override fun update(deltaTime: Double) {
        slideMotor.update(deltaTime)
    }

    fun runToPosition(ticks: Number){
        slideMotor.runToPosition(ticks.toDouble())
    }
}