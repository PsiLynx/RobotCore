package org.ftc3825.subsystem

import org.ftc3825.component.Component
import org.ftc3825.component.TouchSensor
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.inches
import org.ftc3825.util.leftOuttakeMotorName
import org.ftc3825.util.outtakeTouchSensorName
import org.ftc3825.util.rightOuttakeMotorName
import org.ftc3825.util.pid.PIDFGParameters
import kotlin.math.PI
import kotlin.math.abs

object OuttakeArm: Subsystem<Extendo> {
    private val controllerParameters = PIDFGParameters(
        P = 0.0,
        D = 0.0,
    )
    val leftMotor = Motor(
        leftOuttakeMotorName,
        1125,
        FORWARD,
        controllerParameters = controllerParameters,
    )
    val rightMotor = Motor(
        rightOuttakeMotorName,
        1125,
        REVERSE,
        controllerParameters = controllerParameters,
    )
    private val ticksPerRad = leftMotor.ticksPerRev / 2 * PI
    private const val zeroAngle = 0.0 // TODO: change

    val touchSensor = TouchSensor(outtakeTouchSensorName)

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity
    val angle: Double
        get() = position / ticksPerRad

    val isAtBottom: Boolean
        get() = touchSensor.pressed

    override val components
        get() = arrayListOf<Component>(leftMotor, rightMotor)

    init {
        motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
    }

    override fun update(deltaTime: Double) {
        if( isAtBottom ) leftMotor.resetPosition()

        rightMotor.setPower(leftMotor.lastWrite or 0.0)
    }

    fun setPower(power: Double) = run {
        leftMotor.setPower(power)
        rightMotor.setPower(power)
    } withEnd {
        leftMotor.setPower(0.0)
        rightMotor.setPower(0.0)
    }

    fun runToPosition(pos: Double) = (
        run { leftMotor.runToPosition(pos) }
        until {
            abs(leftMotor.position - pos) < 30
            && abs(leftMotor.encoder!!.delta) < 5
        }
        withEnd {
            //setPower(controllerParameters.F.toDouble())
            leftMotor.doNotFeedback()
        }
    )
    fun setAngle(angle: Double) = runToPosition(
        angle
            * ticksPerRad
            / ( 2 * PI )
            - zeroAngle
    )

    fun zero() = run { setPower(-0.5) } until { isAtBottom } withEnd { setPower(0.0) }

}
