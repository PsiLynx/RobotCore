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
import org.firstinspires.ftc.teamcode.util.millis
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline

object HardwareMap{
    lateinit var hardwareMap: HardwareMap

    val frontRight   = motor(0)
    val backRight    = motor(1)
    val backLeft     = motor(2)
    val frontLeft    = motor(3)

    val extendoEncoder    = quadratureEncoder(backLeft)
    val outtakeRelEncoder = quadratureEncoder(backRight)

    val yAxisTouchSensor  = touchSensor(2)
    val xAxisTouchSensor  = touchSensor(4)
    val pinpoint          = goBildaPinpoint(0)

    val leftExtendo  = motor(4)
    val leftOuttake  = motor(5)
    val rightOuttake = motor(6)
    val rightExtendo = motor(7)

    val outtakeGrip  = servo(8)
    val outtakeRoll  = servo(9)
    val outtakePitch = servo(10)
    val intakeGrip   = servo(11)

    val intakeRoll   = servo(15)
    val intakePitch  = servo(16)
    val xAxis        = crServo(17)

    val camera       = camera(0)

    object DeviceTimes {
        val chubMotor = millis(1.5) //TODO: get accurate number
        val exhubMotor = millis(2.0) //TODO: get accurate number

        val chubServo = millis(1.5) //TODO: get accurate number
        val exhubServo = millis(1.5) //TODO: get accurate number
        val shubServo = millis(1.5) //TODO: get accurate number

        val pinpoint = millis(5.0) //TODO: get accurate number
        val octoquad = millis(2.4) //TODO: get accurate number
    }
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
    private fun motor(port: Int) = object : MotorConstructor {
        override val name = "$port"
        override operator fun invoke(
            direction: Component.Direction,
            basePriority: Double,
            priorityScale: Double
        ) = HWQue.managed(Motor(
            name,
            if(port < 4) DeviceTimes.chubMotor
                    else DeviceTimes.exhubMotor,
            direction,
            basePriority,
            priorityScale
        ))
    }

    interface ServoConstructor{
        operator fun invoke(
            basePriority: Double = 1.0,
            priorityScale: Double = 1.0,
            range: Servo.Range = Servo.Range.Default
        ): Servo
    }
    private fun servo(port: Int) = object : ServoConstructor {
        override operator fun invoke(
            basePriority: Double,
            priorityScale: Double,
            range: Servo.Range,
        ) = HWQue.managed(Servo(
            "$port",
            if(port < 4) DeviceTimes.chubMotor
            else DeviceTimes.exhubMotor,
            basePriority,
            priorityScale,
            range
        ))
    }

    interface CrServoConstructor{
        operator fun invoke(
            direction: Component.Direction,
            basePriority: Double = 1.0,
            priorityScale: Double = 1.0,
            range: Servo.Range = Servo.Range.Default
        ): CRServo
    }
    private fun crServo(port: Int) = object : CrServoConstructor{
        override operator fun invoke(
            direction: Component.Direction,
            basePriority: Double,
            priorityScale: Double,
            range: Servo.Range,
        ) = HWQue.managed(CRServo(
            "$port",
            if     (port < 6)  DeviceTimes.chubServo
            else if(port < 12) DeviceTimes.exhubServo
            else               DeviceTimes.shubServo,
            direction,
            basePriority,
            priorityScale,
            range
        ))
    }

    interface AnalogEncoderConstructor {
        operator fun invoke(
            maxVoltage: Double,
            zeroVoltage: Double,
            wheelRadius: Double = 1.0
        ): AnalogEncoder
    }
    private fun analogEncoder(port: Int) = object: AnalogEncoderConstructor {
        override operator fun invoke(
            maxVoltage: Double,
            zeroVoltage: Double,
            wheelRadius: Double
        ) = AnalogEncoder("$port", maxVoltage, zeroVoltage, wheelRadius)
    }

    interface QuadratureEncoderConstructor {
        operator fun invoke(
            direction: Component.Direction,
            ticksPerRev: Double,
            wheelRadius: Double = 1.0
        ): QuadratureEncoder
    }
    private fun quadratureEncoder(port: Int)
        = object: QuadratureEncoderConstructor {
            override operator fun invoke(
                direction: Component.Direction,
                ticksPerRev: Double,
                wheelRadius: Double
            ) = QuadratureEncoder("$port", direction, ticksPerRev, wheelRadius)
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
    private fun touchSensor(port: Int) = object : TouchSensorConstructor {
        override operator fun invoke(default: Boolean)
            = TouchSensor("$port", default)
    }

    interface PinpointConstructor {
        operator fun invoke(priority: Double, ): Pinpoint
    }
    private fun goBildaPinpoint(port: Int) = object : PinpointConstructor {
        override operator fun invoke(priority: Double)
            = HWQue.managed(Pinpoint("$port", priority))
    }

    interface CameraConstructor {
        operator fun invoke(
            resolution: Vector2D,
            pipeLine: OpenCvPipeline,
            rotation: OpenCvCameraRotation
        ): Camera
    }
    private fun camera(port: Int) = object : CameraConstructor {
        override operator fun invoke(
            resolution: Vector2D,
            pipeLine: OpenCvPipeline,
            rotation: OpenCvCameraRotation
        ) = Camera("$port", resolution, pipeLine, rotation)
    }
}