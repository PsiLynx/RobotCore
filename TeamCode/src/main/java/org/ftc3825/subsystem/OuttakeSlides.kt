package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.TouchSensor
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Motor
import org.ftc3825.util.inches
import org.ftc3825.util.leftOuttakeMotorName
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.rightOuttakeMotorName
import kotlin.math.abs

object OuttakeSlides: Subsystem<OuttakeSlides> {
    private val controllerParameters = PIDFGParameters(
        P = 0.016,
        I = 0.0,
        D = 0.02,
        F = 0.1
    )
    val leftMotor = Motor(
        leftOuttakeMotorName,
        1125,
        Motor.Direction.FORWARD,
        controllerParameters = controllerParameters,
        wheelRadius = inches(0.75),
    )
    val rightMotor = Motor(
        rightOuttakeMotorName,
        1125,
        Motor.Direction.REVERSE,
        controllerParameters = controllerParameters,
        wheelRadius = inches(0.75),
    )

    private var setpoint = 0.0

    private val touchSensor: TouchSensor = GlobalHardwareMap.get(TouchSensor::class.java, "slides")

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity

    val isAtBottom: Boolean
        get() = touchSensor.isPressed

    override val components
        get() = arrayListOf<Component>(leftMotor, rightMotor)

    init {
        motors.forEach {
            it.useInternalEncoder()
            it.encoder!!.reversed = 1
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
    }

    override fun update(deltaTime: Double) {
        if( touchSensor.isPressed ) leftMotor.resetPosition()

        components.forEach { it.update(deltaTime) }
        rightMotor.setPower(leftMotor.lastWrite or 0.0)
    }

    fun setPower(power: Double) {
        leftMotor.setPower(power)
        rightMotor.setPower(power)
    }

    fun runToPosition(pos: Double) = (
        run { leftMotor.runToPosition(pos) }
        until {
               abs(this.position - pos) < 5
            && abs(this.leftMotor.encoder!!.delta) < 5
        }
        withEnd {
            setPower(controllerParameters.F.toDouble())
            leftMotor.doNotFeedback()
        }
    )

    fun holdPosition(pos: Double = setpoint) = (
            justUpdate()
                withInit {
                    leftMotor.runToPosition(pos)
                }
            until { setpoint != pos }
    )

    fun extend() = (
        runToPosition(1205.0)
    )

    fun retract() = (
        runToPosition(0.0) withEnd { setPower(0.0); leftMotor.doNotFeedback() }
    )

}
