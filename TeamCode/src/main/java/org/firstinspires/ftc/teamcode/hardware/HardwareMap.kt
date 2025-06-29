package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.component.AnalogEncoder
import org.firstinspires.ftc.teamcode.component.TouchSensor
import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Camera
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.Pinpoint
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardware
import org.firstinspires.ftc.teamcode.hardware.HWManager.qued
import org.firstinspires.ftc.teamcode.util.GoBildaPinpointDriver
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.millis
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.OpenCvWebcam

object HardwareMap{
    lateinit var hardwareMap: HardwareMap

    val frontRight   =   motor(2,  "front right")
    val backRight    =   motor(1,  "back right")
    val backLeft     =   motor(3,  "back left")
    val frontLeft    =   motor(0,  "front left")

    val leftExtendo  =   motor(4,  "left extendo")
    val leftOuttake  =   motor(5,  "left outtake")
    val rightOuttake =   motor(6,  "right outtake")
    val rightExtendo =   motor(7,  "right extendo")

    val outtakeGrip  =   servo(8,  "outtake grip")
    val outtakeRoll  =   servo(9,  "outtake roll")
    val outtakePitch =   servo(10, "outtake pitch")
    val intakeGrip   =   servo(11, "intake grip")

    val intakeRoll   =   servo(15, "intake roll")
    val intakePitch  =   servo(16, "intake pitch")
    val xAxis        = crServo(17, "xAxis")

    val outtakeRelEncoder = quadratureEncoder(1)
    val extendoEncoder    = quadratureEncoder(2)

    val yAxisTouchSensor  = touchSensor(2)
    val xAxisTouchSensor  = touchSensor(4)
    val pinpoint          = goBildaPinpoint(0, "pinpoint")

    val camera            = camera(0)

    object DeviceTimes {
        val chubMotor = millis(1.657)
        val exhubMotor = millis(2.0) //TODO: get accurate number

        val chubServo = millis(1.5) //TODO: get accurate number
        val exhubServo = millis(1.5) //TODO: get accurate number
        val shubServo = millis(1.5) //TODO: get accurate number

        val pinpoint = millis(5.0) //TODO: get accurate number
        val octoquad = millis(2.4) //TODO: get accurate number
    }
    fun init(hardwareMap: HardwareMap){ this.hardwareMap = hardwareMap }

    /*inline fun <reified T: Any> get(
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
    }*/

    interface MotorConstructor{
        operator fun invoke(
            direction: Component.Direction,
            basePriority: Double = 1.0,
            priorityScale: Double = 1.0
        ): Motor
    }
    private fun motor(port: Int, name: String) = object : MotorConstructor {
        override operator fun invoke(
            direction: Component.Direction,
            basePriority: Double,
            priorityScale: Double
        ) = Motor(
            hardwareMap.get(DcMotor::class.java, "$port"),
            name,
            port,
            if(port < 4) DeviceTimes.chubMotor
                    else DeviceTimes.exhubMotor,
            direction,
            basePriority,
            priorityScale
        ).qued()
    }

    interface ServoConstructor{
        operator fun invoke(
            basePriority: Double = 1.0,
            priorityScale: Double = 1.0,
            range: Servo.Range = Servo.Range.Default
        ): Servo
    }
    private fun servo(port: Int, name: String) = object : ServoConstructor {
        override operator fun invoke(
            basePriority: Double,
            priorityScale: Double,
            range: Servo.Range,
        ) = Servo(
            hardwareMap.get(
                com.qualcomm.robotcore.hardware.Servo::class.java,
                "$port"
            ) as ServoImplEx,
            name,
            port,
            if(port < 4) DeviceTimes.chubMotor
            else DeviceTimes.exhubMotor,
            basePriority,
            priorityScale,
            range
        ).qued()
    }

    interface CrServoConstructor{
        operator fun invoke(
            direction: Component.Direction,
            basePriority: Double = 1.0,
            priorityScale: Double = 1.0,
            range: Servo.Range = Servo.Range.Default
        ): CRServo
    }
    private fun crServo(port: Int, name: String) = object : CrServoConstructor{
        override operator fun invoke(
            direction: Component.Direction,
            basePriority: Double,
            priorityScale: Double,
            range: Servo.Range,
        ) = CRServo(
            hardwareMap.get(
                com.qualcomm.robotcore.hardware.Servo::class.java,
                "$port"
            ) as ServoImplEx,
            name,
            port,
            if     (port < 6)  DeviceTimes.chubServo
            else if(port < 12) DeviceTimes.exhubServo
            else               DeviceTimes.shubServo,
            direction,
            basePriority,
            priorityScale,
            range
        ).qued()
    }

    interface AnalogEncoderConstructor {
        operator fun invoke(
            maxVoltage: Double,
            zeroVoltage: Double,
            wheelRadius: Double = 1.0
        ): AnalogEncoder
    }
    private fun analogEncoder(port: Int) = object:
        AnalogEncoderConstructor {
        override operator fun invoke(
            maxVoltage: Double,
            zeroVoltage: Double,
            wheelRadius: Double
        ) = AnalogEncoder(
            port,
            maxVoltage,
            zeroVoltage,
            wheelRadius
        )
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
            ) = QuadratureEncoder(
                port,
                direction,
                ticksPerRev,
                wheelRadius
            )
        }

    interface TouchSensorConstructor {
        operator fun invoke(default: Boolean = false): TouchSensor
    }
    private fun touchSensor(port: Int) = object : TouchSensorConstructor {
        override operator fun invoke(default: Boolean)
            = TouchSensor(port, default)
    }

    interface PinpointConstructor {
        operator fun invoke(priority: Double): Pinpoint
    }
    private fun goBildaPinpoint(port: Int, uniqueName: String)
        = object : PinpointConstructor {
            override operator fun invoke(priority: Double)
                = Pinpoint(
                    hardwareMap.get(GoBildaPinpointDriver::class.java, "$port"),
                    uniqueName,
                    priority
                ).qued().logged() as Pinpoint
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
        ) = Camera(
            OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName::class.java, "$port"),
                hardwareMap.appContext.resources.getIdentifier(
                    "cameraMonitorViewId",
                    "id",
                    hardwareMap.appContext.packageName
                )
            ),
            resolution,
            pipeLine,
            rotation
        )
    }
}