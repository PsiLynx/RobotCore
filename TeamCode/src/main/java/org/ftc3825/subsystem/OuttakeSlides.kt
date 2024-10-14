package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.Command
import org.ftc3825.component.Motor
import org.ftc3825.util.centimeters
import org.ftc3825.util.inches
import org.ftc3825.util.outtakeMotorName
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.command.internal.CommandScheduler

object OuttakeSlides: Subsystem<OuttakeSlides>() {
    val leftMotor = Motor(
        outtakeMotorName,
        rpm = 1125,
        wheelRadius = inches(0.75),
        controllerParameters = PIDFGParameters(
            P = 0.0003,
            I = 0.000,
            D = 0.001,
            F = 0,
        )
    )
    val rightMotor = Motor(
        outtakeMotorName,
        rpm = 1125,
        wheelRadius = inches(0.75),
        controllerParameters = PIDFGParameters(
            P = 0.0003,
            D = 0.001,
        )
    )

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity

    override val motors
        get() = arrayListOf(leftMotor, rightMotor)


    init {
        leftMotor.useInternalEncoder()
        rightMotor.useInternalEncoder()

        rightMotor.follow( leftMotor )
    }

    override fun update(deltaTime: Double) {
        motors.forEach { it.update(deltaTime) }
    }

    fun runToPosition(ticks: Number){
        leftMotor.runToPosition(ticks.toDouble())
    }

    fun extend() = Command(
        initialize = { runToPosition(1000) },
        end = { _ -> leftMotor.doNotFeedback() },
        isFinished = { true },
        requirements = arrayListOf<Subsystem<*>>(this)
    )

    fun retract() = Command(
        initialize = { runToPosition(0) },
        end = { _ -> leftMotor.doNotFeedback() },
        isFinished = { true }
    )

}
