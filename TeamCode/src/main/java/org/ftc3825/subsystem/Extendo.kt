package org.ftc3825.subsystem

import org.ftc3825.command.internal.Command
import org.ftc3825.component.CRServo
import org.ftc3825.component.Camera
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.component.QuadratureEncoder
import org.ftc3825.component.TouchSensor
import org.ftc3825.cv.GamePiecePipeLine
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Vector2D
import org.ftc3825.util.degrees
import org.ftc3825.util.fisheyeLensName
import org.ftc3825.util.inches
import org.ftc3825.util.leftExtendoMotorName
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.rightExtendoMotorName
import org.ftc3825.util.xAxisServoName
import org.ftc3825.util.xAxisTouchSensorName
import org.ftc3825.util.yAxisTouchSensorName
import kotlin.math.abs

object Extendo: Subsystem<Extendo> {
    private val yControllerParameters = PIDFGParameters(
        P = 0.007,
        D = 0.007,
    )
    private val xControllerParameters = PIDFGParameters(
        P = 0.007,
        D = 0.007,
    )
    private val leftMotor = Motor(
        leftExtendoMotorName,
        1125,
        FORWARD,
        controllerParameters = yControllerParameters,
        wheelRadius = inches(0.75),
    )
    private val rightMotor = Motor(
        rightExtendoMotorName,
        1125,
        REVERSE,
        controllerParameters = yControllerParameters,
        wheelRadius = inches(0.75),
    )
    val xAxisServo = CRServo(xAxisServoName)
    const val xMax = 10.0 //TODO: Change
    const val yMax = 10.0 //TODO: Change

    val yTouchSensor = TouchSensor(yAxisTouchSensorName)
    val xTouchSensor = TouchSensor(xAxisTouchSensorName)

    private val resolution = Vector2D(640, 480)
    private val pipeLine = GamePiecePipeLine()
    private val camera = Camera(fisheyeLensName, resolution, pipeLine)

    val position: Vector2D
        get() = Vector2D(xAxisServo.position, leftMotor.position)
    val velocity: Vector2D
        get() = Vector2D(xAxisServo.velocity, leftMotor.velocity)

    val xPressed: Boolean
        get() = xTouchSensor.pressed
    val yPressed: Boolean
        get() = yTouchSensor.pressed

    override val components = arrayListOf(
        leftMotor,
        rightMotor,
        xAxisServo,
        xTouchSensor,
        yTouchSensor
    )

    init {
        motors.forEach {
            it.useInternalEncoder()
            it.encoder!!.direction = REVERSE
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
        xAxisServo.direction = REVERSE
        xAxisServo.useEncoder(QuadratureEncoder(rightExtendoMotorName, REVERSE))
    }

    val samples: List<Pose2D>
        get() = pipeLine.samples.map {
            ( Pose2D(it.center.x, it.center.y, degrees(it.angle) )
                - ( resolution / 2 ) // center it
            )
        }

    override fun update(deltaTime: Double) {
        if(yPressed) leftMotor.resetPosition()
        if(xPressed) xAxisServo.resetPosition()

        rightMotor.setPower(leftMotor.lastWrite or 0.0)
    }
    fun setPower(power: Vector2D) {
        leftMotor.setPower(power.y)
        rightMotor.setPower(power.y)
        xAxisServo.power = power.x
    }

    fun setPowerCommand(power: Vector2D) = (
        run { setPower(power) }
        withEnd { setPower( Vector2D(0, 0) ) }
    )

    fun setPowerCommand(xPower: Number, yPower: Number) = setPowerCommand(
        Vector2D(xPower, yPower)
    )

    fun setPosition(pos: Vector2D) = setX(pos.x) parallelTo setY(pos.y)

    fun setX(pos: Double) = (
        run { xAxisServo.runToPosition(pos) }
        until {
            abs(position.x - pos) < 0.1
            && abs(velocity.x) < 5
        }
        withEnd { xAxisServo.doNotFeedback() }
    )
    fun setY(pos: Double) = (
        run { leftMotor.runToPosition(pos) }
        until {
            abs(position.y - pos) < 0.1
            && abs(velocity.y) < 5
        }
        withEnd { leftMotor.doNotFeedback() }
    )

    fun extend() = setY(yMax)
    fun retract() = setY(0.0) until { yPressed }

}
