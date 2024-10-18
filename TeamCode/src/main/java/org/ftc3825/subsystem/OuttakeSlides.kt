package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.Command
import org.ftc3825.component.Motor
import org.ftc3825.util.centimeters
import org.ftc3825.util.inches
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.util.leftOuttakeMotorName
import org.ftc3825.util.rightOuttakeMotorName

object OuttakeSlides: Subsystem<OuttakeSlides>() {
    val leftMotor = Motor(
        leftOuttakeMotorName,
        1125,
        Motor.Direction.FORWARD,
        wheelRadius = inches(0.75),
        controllerParameters = PIDFGParameters(
            P = 0.0003,
            I = 0.000,
            D = 0.001,
            F = 0,
        )
    )
    val rightMotor = Motor(
        rightOuttakeMotorName,
        1125,
        Motor.Direction.REVERSE,
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
        motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
        rightMotor.follow( leftMotor )
    }

    override fun update(deltaTime: Double) {
        motors.forEach { it.update(deltaTime) }
    }

    fun runToPosition(ticks: Number){
        leftMotor.runToPosition(ticks.toDouble())
    }

    fun setPower(power: Double) = leftMotor.setPower(power)

    fun moveToBar() = Command(
        initialize = { runToPosition(500) },
        end = { _ -> leftMotor.doNotFeedback() },
        isFinished = { leftMotor.error < 20 },
        requirements = arrayListOf(this)
    )

    fun moveBelowBar() = Command(
        initialize = { runToPosition(400) },
        end = { _ -> leftMotor.doNotFeedback() },
        isFinished = { leftMotor.error < 20 },
        requirements = arrayListOf(this)
    )


    fun extend() = Command(
        initialize = { runToPosition(1000) },
        end = { _ -> leftMotor.doNotFeedback() },
        isFinished = { leftMotor.error < 20 },
        requirements = arrayListOf<Subsystem<*>>(this)
    )

    fun retract() = Command(
        initialize = { runToPosition(0) },
        end = { _ -> leftMotor.doNotFeedback() },
        isFinished = { leftMotor.error < 20 },
        requirements = arrayListOf<Subsystem<*>>(this)
    )

}
