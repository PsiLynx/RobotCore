package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.navigation.Velocity
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.OctoQuadFWv3
import org.firstinspires.ftc.teamcode.component.AnalogDistanceSensor
import org.firstinspires.ftc.teamcode.component.AnalogEncoder
import org.firstinspires.ftc.teamcode.component.TouchSensor
import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Camera
import org.firstinspires.ftc.teamcode.component.OpenCvCamera
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.DigitalSensor
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.OctoQuad
import org.firstinspires.ftc.teamcode.component.PWMLight
import org.firstinspires.ftc.teamcode.component.Pinpoint
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.millis
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import kotlin.jvm.java

object HardwareMap {
     var hardwareMap: HardwareMap? = null

    val frontLeft    = motor(0)
    val backRight    = motor(1)
    val backLeft     = motor(2)
    val frontRight   = motor(3)

    val shooterLeft  = motor(4)
    val shooterRight = motor(4)
    val intake       = motor(6)
    val turret       = motor(7)

    val hood         = servo(0)
    val backLight    = pwmLight(1)
    val frontLight   = pwmLight(2)

    val propeller    = servo(6)

    val blocker      = servo(13)



    val shooterEncoder = quadratureEncoder(0)

    // Change as needed
    val turretEncoder = quadratureEncoder(1)

    val pinpoint       = goBildaPinpoint(0)
    val octoQuad       = octoQuadLocalizer(1)
    val obeliskCamera  = camera(0)


    val colorSensor = digitalSensor(0)
    val topSensor = analogDistanceSensor(0)
    val bottomSensor = analogDistanceSensor(1)

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
            lowPassDampening: Double = 0.0,
        ): Motor
    }
    private fun motor(port: Int) = object : MotorConstructor {
        override operator fun invoke(
            direction: Component.Direction,
            lowPassDampening: Double,
        ) = Motor(
            { hardwareMap?.get(DcMotor::class.java, "m$port") },
            port,
            direction,
            lowPassDampening
        )
    }

    interface PWMLightConstructor{
        operator fun invoke(
        ): PWMLight
    }
    private fun pwmLight(port: Int) = object : PWMLightConstructor {
        override operator fun invoke( ) = PWMLight(
            {
                hardwareMap?.get(
                    com.qualcomm.robotcore.hardware.Servo::class.java,
                    "s$port"
                ) as ServoImplEx
            },
            port,
        )
    }

    interface ServoConstructor{
        operator fun invoke(
            range: Servo.Range = Servo.Range.Default
        ): Servo
    }
    private fun servo(port: Int) = object : ServoConstructor {
        override operator fun invoke(
            range: Servo.Range,
        ) = Servo(
            {
                hardwareMap?.get(
                    com.qualcomm.robotcore.hardware.Servo::class.java,
                    "s$port"
                ) as ServoImplEx
            },
            port,
            range
        )
    }

    interface CrServoConstructor{
        operator fun invoke(
            direction: Component.Direction,
            range: Servo.Range = Servo.Range.Default,
        ): CRServo
    }
    private fun crServo(port: Int) = object : CrServoConstructor{
        override operator fun invoke(
            direction: Component.Direction,
            range: Servo.Range,
        ) = CRServo(
            {
                hardwareMap?.get(
                    com.qualcomm.robotcore.hardware.Servo::class.java,
                    "s$port"
                ) as ServoImplEx
            },
            port,
            direction,
            range
        )
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
            { hardwareMap?.get(AnalogInput::class.java, "a$port") },
            maxVoltage,
            zeroVoltage,
            wheelRadius
        )
    }

    interface AnalogDistanceSensorConstructor {
        operator fun invoke(
            minDist: Double = 0.0,
            maxDist: Double = 1.0,
            zeroVoltage: Double = 0.0,
            maxVoltage: Double = 3.3,
        ): AnalogDistanceSensor
    }
    private fun analogDistanceSensor(port: Int) = object: AnalogDistanceSensorConstructor {
        override operator fun invoke(
            minDist: Double,
            maxDist: Double,
            zeroVoltage: Double,
            maxVoltage: Double,
        ) = AnalogDistanceSensor(
            { hardwareMap?.get(AnalogInput::class.java, "a$port") },
            minDist,
            maxDist,
            zeroVoltage,
            maxVoltage,
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
                { hardwareMap?.get(DcMotor::class.java, "m$port") },
                direction,
                ticksPerRev,
                wheelRadius
            )
        }

    interface TouchSensorConstructor {
        operator fun invoke(default: Boolean = false): TouchSensor
    }
    private fun touchSensor(port: Int) = object : TouchSensorConstructor {
        override operator fun invoke(default: Boolean) = TouchSensor(
            { hardwareMap?.get(DigitalChannel::class.java, "d$port") },
            default
        )
    }

    interface DigitalSensorConstructor {
        operator fun <T> invoke(
            trueValue: T,
            falseValue: T
        ) : DigitalSensor<T>
    }
    private fun digitalSensor(port: Int) = object : DigitalSensorConstructor {
        override operator fun <T> invoke(
            trueValue: T,
            falseValue: T
        ) = DigitalSensor(
            { hardwareMap?.get(DigitalChannel::class.java, "d$port") },
            trueValue,
            falseValue
        )
    }

    interface PinpointConstructor {
        operator fun invoke(): Pinpoint
    }
    private fun goBildaPinpoint(port: Int)
        = object : PinpointConstructor {
            override operator fun invoke() = Pinpoint {
                hardwareMap?.get(
                    GoBildaPinpointDriver::class.java,
                    "i$port"
                )
            }
    }

    interface OctoQuadConstructor {
        operator fun invoke(
            xPort: Int,
            yPort: Int,
            ticksPerMM: Double,
            offset: Vector2D,
            xDirection: Component.Direction,
            yDirection: Component.Direction,
            headingScalar: Double,
            velocityInterval: Int = 25
        ): OctoQuad
    }
    private fun octoQuadLocalizer(port: Int)
            = object : OctoQuadConstructor {
        override operator fun invoke(
            xPort: Int,
            yPort: Int,
            ticksPerMM: Double,
            offset: Vector2D,
            xDirection: Component.Direction,
            yDirection: Component.Direction,
            headingScalar: Double,
            velocityInterval: Int
        ) = OctoQuad(
            {
                hardwareMap?.get(
                    OctoQuadFWv3::class.java,
                    "i$port"
                )
            },
            xPort,
            yPort,
            ticksPerMM,
            offset,
            xDirection,
            yDirection,
            headingScalar,
            velocityInterval,
        )
    }

    interface OpenCvCameraConstructor {
        operator fun invoke(
            resolution: Vector2D,
            pipeLine: OpenCvPipeline,
            rotation: OpenCvCameraRotation
        ): OpenCvCamera
    }
    private fun openCvCamera(port: Int) = object : OpenCvCameraConstructor {
        override operator fun invoke(
            resolution: Vector2D,
            pipeLine: OpenCvPipeline,
            rotation: OpenCvCameraRotation
        ) = OpenCvCamera(
            {
                if(hardwareMap != null) {
                    OpenCvCameraFactory.getInstance().createWebcam(
                        hardwareMap!!.get(WebcamName::class.java, "c$port"),
                        hardwareMap!!.appContext.resources.getIdentifier(
                            "cameraMonitorViewId",
                            "id",
                            hardwareMap!!.appContext.packageName
                        )
                    )
                } else null
            },
            resolution,
            pipeLine,
            rotation
        )
    }

    interface CameraConstructor {
        operator fun invoke(
            resolution: Vector2D,
            cameraPosition: Vector3D,
            cameraRotation: YawPitchRollAngles,
        ): Camera
    }
    private fun camera(port: Int) = object : CameraConstructor {
        override operator fun invoke(
            resolution: Vector2D,
            cameraPosition: Vector3D,
            cameraRotation: YawPitchRollAngles,
        ) = Camera(
            {
                hardwareMap?.get(WebcamName::class.java, "c$port")
            },
            resolution,
            cameraPosition,
            cameraRotation
        )
    }
}
