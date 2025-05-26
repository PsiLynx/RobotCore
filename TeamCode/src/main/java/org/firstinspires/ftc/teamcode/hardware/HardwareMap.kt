package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.AnalogEncoder
import org.firstinspires.ftc.teamcode.component.TouchSensor
import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Camera
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.Pinpoint
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline

object HardwareMap{
    lateinit var hardwareMap: HardwareMap

    val frontRight   = motor("0")
    val backRight    = motor("1")
    val backLeft     = motor("2")
    val frontLeft    = motor("3")

    val extendoEncoder    = quadratureEncoder(backLeft)
    val outtakeRelEncoder = quadratureEncoder(backRight)

    val yAxisTouchSensor  = touchSensor("2")
    val xAxisTouchSensor  = touchSensor("4")
    val pinpoint          = goBildaPinpoint("pinpoint")

    val leftExtendo  = motor("4")
    val leftOuttake  = motor("5")
    val rightOuttake = motor("6")
    val rightExtendo = motor("7")

    val outtakeGrip  = servo("8")
    val outtakeRoll  = servo("9")
    val outtakePitch = servo("10")
    val intakeGrip   = servo("11")

    val intakeRoll   = servo("15")
    val intakePitch  = servo("16")
    val xAxis        = crServo("17")

    val camera       = camera("0")

    fun init(hardwareMap: HardwareMap){ this.hardwareMap = hardwareMap }

    inline fun <reified T: Any> get(
        classOrInterface: Class<out T>,
        deviceName: String
    ): T {
        return hardwareMap.get(classOrInterface, deviceName)
    }

    fun getIdentifier(name: String, defType: String, defPackage: String) =
        hardwareMap.appContext.resources.getIdentifier(name, defType, defPackage)

    object appContext {
        val packageName: String
            get() = hardwareMap.appContext.packageName
    }

    interface MotorConstructor{
        val name: String
        operator fun invoke(
            direction: Component.Direction,
            basePriority: Double = 1.0,
            priorityScale: Double = 1.0
        ): Motor
    }
    private fun motor(name: String) = object : MotorConstructor {
        override val name = name
        override operator fun invoke(
            direction: Component.Direction,
            basePriority: Double,
            priorityScale: Double
        ) = HWQue.motor(name, direction, basePriority, priorityScale)
    }

    interface ServoConstructor{
        operator fun invoke(
            basePriority: Double = 1.0,
            priorityScale: Double = 1.0,
            range: Servo.Range = Servo.Range.Default
        ): Servo
    }
    private fun servo(name: String) = object : ServoConstructor {
        override operator fun invoke(
            basePriority: Double,
            priorityScale: Double,
            range: Servo.Range,
        ) = HWQue.servo(name, basePriority, priorityScale, range)
    }

    interface CrServoConstructor{
        operator fun invoke(
            direction: Component.Direction,
            basePriority: Double = 1.0,
            priorityScale: Double = 1.0,
            range: Servo.Range = Servo.Range.Default
        ): CRServo
    }
    private fun crServo(name: String) = object : CrServoConstructor{
        override operator fun invoke(
            direction: Component.Direction,
            basePriority: Double,
            priorityScale: Double,
            range: Servo.Range,
        ) = HWQue.crServo(name, direction, basePriority, priorityScale, range)
    }

    interface AnalogEncoderConstructor {
        operator fun invoke(
            maxVoltage: Double,
            zeroVoltage: Double,
            wheelRadius: Double = 1.0
        ): AnalogEncoder
    }
    private fun analogEncoder(name: String) = object: AnalogEncoderConstructor {
        override operator fun invoke(
            maxVoltage: Double,
            zeroVoltage: Double,
            wheelRadius: Double
        ) = AnalogEncoder(name, maxVoltage, zeroVoltage, wheelRadius)
    }

    interface QuadratureEncoderConstructor {
        operator fun invoke(
            direction: Component.Direction,
            ticksPerRev: Double,
            wheelRadius: Double = 1.0
        ): QuadratureEncoder
    }
    private fun quadratureEncoder(name: String)
        = object: QuadratureEncoderConstructor {
            override operator fun invoke(
                direction: Component.Direction,
                ticksPerRev: Double,
                wheelRadius: Double
            ) = QuadratureEncoder(name, direction, ticksPerRev, wheelRadius)
        }
    private fun quadratureEncoder(motor: MotorConstructor)
        = object: QuadratureEncoderConstructor {
            override operator fun invoke(
                direction: Component.Direction,
                ticksPerRev: Double,
                wheelRadius: Double
            ) = QuadratureEncoder(motor.name, direction, ticksPerRev, wheelRadius)
        }

    interface TouchSensorConstructor {
        operator fun invoke(default: Boolean = false): TouchSensor
    }
    private fun touchSensor(name: String) = object : TouchSensorConstructor {
        override operator fun invoke(default: Boolean)
            = TouchSensor(name, default)
    }

    interface PinpointConstructor {
        operator fun invoke(priority: Double, ): Pinpoint
    }
    private fun goBildaPinpoint(name: String) = object : PinpointConstructor {
        override operator fun invoke(priority: Double)
            = HWQue.pinpoint(name, priority)
    }

    interface CameraConstructor {
        operator fun invoke(
            resolution: Vector2D,
            pipeLine: OpenCvPipeline,
            rotation: OpenCvCameraRotation
        ): Camera
    }
    private fun camera(name: String) = object : CameraConstructor {
        override operator fun invoke(
            resolution: Vector2D,
            pipeLine: OpenCvPipeline,
            rotation: OpenCvCameraRotation
        ) = Camera(name, resolution, pipeLine, rotation)
    }
}