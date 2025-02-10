package org.ftc3825.subsystem

import com.acmerobotics.dashboard.config.Config
import org.ftc3825.component.CRServo
import org.ftc3825.component.Camera
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.component.QuadratureEncoder
import org.ftc3825.component.TouchSensor
import org.ftc3825.cv.GamePiecePipeLine
import org.ftc3825.subsystem.ExtendoConf.yP
import org.ftc3825.subsystem.ExtendoConf.yD
import org.ftc3825.subsystem.ExtendoConf.xP
import org.ftc3825.subsystem.ExtendoConf.xD
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Vector2D
import org.ftc3825.util.degrees
import org.ftc3825.util.fisheyeLensName
import org.ftc3825.util.inches
import org.ftc3825.util.leftExtendoMotorName
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.pid.pdControl
import org.ftc3825.util.rightExtendoMotorName
import org.ftc3825.util.xAxisServoName
import org.ftc3825.util.xAxisTouchSensorName
import org.ftc3825.util.yAxisTouchSensorName
import kotlin.math.abs
import org.ftc3825.subsystem.ExtendoConf.cameraExposureMs
import org.ftc3825.subsystem.ExtendoConf.lastExposure
import org.ftc3825.subsystem.ExtendoConf.yAbsF
import org.ftc3825.subsystem.ExtendoConf.yRelF
import org.ftc3825.util.extendoEncoderName
import org.ftc3825.util.millimeters
import org.ftc3825.util.xAxisEncoderMotorName
import org.openftc.easyopencv.OpenCvCameraRotation

@Config
object ExtendoConf {
    @JvmField var yP = 3.0
    @JvmField var yD = 0.5
    @JvmField var yAbsF = 0.1
    @JvmField var yRelF = 0.25
    @JvmField var xP = 10.0
    @JvmField var xD = 0.0
    @JvmField var cameraExposureMs = 30.0
    var lastExposure = 30.0
}
object Extendo: Subsystem<Extendo> {
    val yControllerParameters = PIDFGParameters(
        { yP },
        { yD },
        absF = { yAbsF },
        relF = { yRelF },
    )
    val xControllerParameters = PIDFGParameters(
        { xP },
        { xD },
    )

    val clipPositions = arrayOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0)//TODO: tune
    val leftMotor = Motor(
        leftExtendoMotorName,
        1150,
        REVERSE,
        controllerParameters = yControllerParameters,
        wheelRadius = millimeters(32)
    )
    private val rightMotor = Motor(
        rightExtendoMotorName,
        1150,
        REVERSE,
    )
    val xAxisServo = CRServo(
        xAxisServoName,
        REVERSE,
        ticksPerRev = 2048.0,
        wheelRadius = millimeters(12.73)
    )
    const val xMax = 10.0 //TODO: Change
    const val yMax = 10.0 //TODO: Change
    val yTouchSensor = TouchSensor(yAxisTouchSensorName, defualt = true)
    val xTouchSensor = TouchSensor(xAxisTouchSensorName, defualt = true)

    private val resolution = Vector2D(640, 480)
    private val pipeLine = GamePiecePipeLine()
    val camera = Camera(
        fisheyeLensName,
        resolution,
        pipeLine,
        OpenCvCameraRotation.SIDEWAYS_LEFT
    )

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
        leftMotor.encoder = QuadratureEncoder(
            extendoEncoderName,
            REVERSE,
        )
        xAxisServo.useEncoder(QuadratureEncoder(xAxisEncoderMotorName, FORWARD))
        xAxisServo.initializeController(xControllerParameters)
        leftMotor.initializeController(yControllerParameters)
        //camera.exposureMs = 30.0
    }

    val samples: List<Pose2D>
        get() = pipeLine.samples.map {
            (
                Pose2D(it.center.x, it.center.y, degrees(it.angle))
                - ( resolution / 2 ) // center it
            )
        }
    val closestSample: Pose2D
        get() = samples.minBy { it.mag }

    override fun update(deltaTime: Double) {
        if(cameraExposureMs != lastExposure){
            //camera.exposureMs = cameraExposureMs
            lastExposure = cameraExposureMs
        }

        if(yPressed) leftMotor.resetPosition()
        if(xPressed) xAxisServo.resetPosition()

        //rightMotor.power = leftMotor.lastWrite or 0.0
    }
    fun setPower(power: Vector2D) {
        leftMotor.power  = power.y
        rightMotor.power = power.y
        xAxisServo.power = power.x
    }
    fun setPowerCommand(power: Vector2D) = run { setPower(power) }
    fun setPowerCommand(x: Double, y: Double) = run {
        setPower( Vector2D(x, y) )
    }

    fun setPosition(pos: Vector2D) = setX(pos.x) parallelTo setY(pos.y)


    fun setX(pos: Double) = (
        run { xAxisServo.runToPosition(pos) }
        until {
            abs(position.x - pos) < 0.0001
            && abs(velocity.x) < 0.1
        }
        withEnd {
            xAxisServo.doNotFeedback()
            xAxisServo.power = 0.0
        }
    )
    fun setY(pos: Double) = (
        run {
            leftMotor.runToPosition(pos)
            rightMotor.power = leftMotor.lastWrite or 0.0
        }
        until {
            abs(position.y - pos) < 0.001
            && abs(velocity.y) < 0.1
        }
        withEnd {
            leftMotor.doNotFeedback()
            leftMotor.power = 0.0
            rightMotor.power = 0.0
        }
    )

    fun extend() = setY(yMax)
    fun retract() = setY(0.0) until { yPressed }

    fun centerOnSample() = run {
        setPower(
            pdControl(
                closestSample.vector,
                velocity,
                0.01,
                0.0 //TODO: Tune
            )
        )
    } until { closestSample.mag < 30 }
    fun transferPos() = setPosition(Vector2D(xMax/2, 2.6))//TODO: Tune
    fun clippingPosition(clip: Int) = setPosition(
        Vector2D(
            clipPositions[clip],
            3.4 //TODO: Tune
        )
    )
}
