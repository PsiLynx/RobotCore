package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.Command
import org.ftc3825.component.Motor
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.centimeters
import org.ftc3825.util.outtakeMotorName
import org.ftc3825.util.pid.PIDFGParameters

object OuttakeSlides: Subsystem<OuttakeSlides> {
    override var initialized = false

//    lateinit var motor: Motor
//
//    val position: Double
//        get() = motor.position
//    val velocity: Double
//        get() = motor.velocity
//
//    override val motors
//        get() = arrayListOf(motor)
    override val motors: ArrayList<Motor>
        get() = arrayListOf()

    override fun init(hardwareMap: HardwareMap) {
//        if(!initialized) {
//            motor = Motor(
//                outtakeMotorName,
//                hardwareMap,
//                rpm = 435,
//                wheelRadius = centimeters(1),
//                controllerParameters = PIDFGParameters(
//                    P = 0.0003,
//                    I = 0.000,
//                    D = 0.001,
//                    F = 0,
//                )
//            )
//            motor.useInternalEncoder()
//        }
        initialized = true
    }

    override fun update(deltaTime: Double) {
//        motor.update(deltaTime)
    }

    fun runToPosition(ticks: Number){
//        motor.runToPosition(ticks.toDouble())
    }

    fun extend() = Command(
        initialize = { runToPosition(1000) },
//        end = { _ -> motor.doNotFeedback() },
        isFinished = { true }
    )

    fun retract() = Command(
        initialize = { runToPosition(0) },
//        end = { _ -> motor.doNotFeedback() },
        isFinished = { true }
    )

}