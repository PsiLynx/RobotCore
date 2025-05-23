package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.component.Camera
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.HWManager
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.component.TouchSensor
import org.firstinspires.ftc.teamcode.cv.GamePiecePipeLine
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.yP
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.yD
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.yAbsF
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.yRelF
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.xP
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.xD
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.transferY
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.transferX
import org.firstinspires.ftc.teamcode.subsystem.ExtendoConf.xF
import org.firstinspires.ftc.teamcode.util.control.PIDFController
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.fisheyeLensName
import org.firstinspires.ftc.teamcode.util.leftExtendoMotorName
import org.firstinspires.ftc.teamcode.util.control.pdControl
import org.firstinspires.ftc.teamcode.util.rightExtendoMotorName
import org.firstinspires.ftc.teamcode.util.xAxisServoName
import org.firstinspires.ftc.teamcode.util.xAxisTouchSensorName
import org.firstinspires.ftc.teamcode.util.yAxisTouchSensorName
import kotlin.math.abs
import org.firstinspires.ftc.teamcode.util.extendoEncoderName
import org.firstinspires.ftc.teamcode.util.millimeters
import org.firstinspires.ftc.teamcode.util.xAxisEncoderMotorName
import org.openftc.easyopencv.OpenCvCameraRotation

@Config
object ExtendoConf {
    @JvmField var yP = 5.0
    @JvmField var yD = 0.0
    @JvmField var yAbsF = 0.0
    @JvmField var yRelF = 0.0
    @JvmField var xP = 1.0
    @JvmField var xD = 0.0
    @JvmField var xF = 0.15
    @JvmField var transferY = 0.0
    @JvmField var transferX = 0.39
    @JvmField var cameraExposureMs = 30.0
    @JvmField var useComp = true
    var lastExposure = 30.0
}
object Extendo: Subsystem<Extendo> {
    val yController = PIDFController(
        { yP },
        { yD },
        absF = { yAbsF },
        relF = { yRelF },
        pos = { leftMotor.position },
        apply = {
            leftMotor .compPower(it)
            rightMotor.compPower(it)
        },
    )
    val xController= PIDFController(
        { xP },
        { xD },
        relF = { xF },
        pos = { xAxisServo.position },
        apply = { xAxisServo.power = it },
    )

    val targetPos: Vector2D get() = Vector2D(
        xController.targetPosition,
        yController.targetPosition
    )

    val leftMotor = Motor(
        leftExtendoMotorName,
        1150,
        REVERSE,
        wheelRadius = millimeters(32)
    )
    private val rightMotor = HWManager.motor(
        rightExtendoMotorName,
        1150,
        FORWARD,
    )
    val xAxisServo = HWManager.crServo(
        xAxisServoName,
        FORWARD,
        ticksPerRev = 2048.0,
        wheelRadius = millimeters(12.73)
    )
    const val yMax = 1.1 //TODO: Change
    val yTouchSensor = TouchSensor(yAxisTouchSensorName, default = true)
    val xTouchSensor = TouchSensor(xAxisTouchSensorName, default = true)

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
    )

    init {
        leftMotor.encoder = QuadratureEncoder(
            extendoEncoderName,
            REVERSE,
        )
        motors.forEach { it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)}
        xAxisServo.useEncoder(QuadratureEncoder(xAxisEncoderMotorName, FORWARD))
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
        if(yPressed) leftMotor.resetPosition()
        if(xPressed) xAxisServo.resetPosition()

        yController.updateController(deltaTime)
    }
    fun setPower(power: Vector2D) {
        leftMotor.power  = power.y
        rightMotor.power = power.y
        xAxisServo.power = power.x
    }
    fun setPowerCommand(power: Vector2D) = (
        run { setPower(power) }
        withEnd { setPower(Vector2D(0.0, 0.0)) }
    )
    fun setPowerCommand(x: Double, y: Double) = run {
        setPower( Vector2D(x, y) )
    }

    fun setPosition(pos: Vector2D) = setX(pos.x) parallelTo setY(pos.y)
    fun setPosition(pos: () -> Vector2D) =
        setX(pos().x) parallelTo setY(pos().y)


    fun setX(pos: () -> Double) = (
        run { xController.targetPosition = pos() }
        until {
            abs(position.x - pos()) < 0.05
            && abs(velocity.x) < 0.1
        }
        withEnd { xAxisServo.power = 0.0 }
    )
    fun setY(pos: () -> Double) = (
        run { yController.targetPosition = pos() }
        until {
            (
                abs(position.y - pos()) < 0.05
                && abs(velocity.y) < 0.1
            ) || ( pos() == 0.0 && yPressed )
        }
        withEnd {
            leftMotor.power = 0.0
            rightMotor.power = 0.0
        }
    )
    fun setX(pos: Double) = setX { pos }
    fun setY(pos: Double) = setY { pos }

    fun extend() = setY { yMax }

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
    fun transferPos() = setPosition { Vector2D(transferX, transferY) }
    fun transferX() = setX { transferX }
}
