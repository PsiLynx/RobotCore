package org.firstinspires.ftc.teamcode.component

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.GoBildaOdometryPods
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.fromSDKPose
import kotlin.math.PI

class Pinpoint(
    private val deviceSupplier: () -> GoBildaPinpointDriver?,
): Component() {

    private var _hwDeviceBacker: GoBildaPinpointDriver? = null
    override val hardwareDevice: GoBildaPinpointDriver get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }

    var startPos = Pose2D(0, 0, PI / 2)

    var position: Pose2D = hardwareDevice.position.fromSDKPose()
        private set

    var velocity = Pose2D(
        hardwareDevice.getVelX(DistanceUnit.INCH),
        hardwareDevice.getVelY(DistanceUnit.INCH),
        hardwareDevice.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS),
    )
        internal set

    var posBad = false
        internal set
    var velBad = false
        internal set

    private var ppPos = Pose2D()
    private var ppVel = Pose2D()

    var xEncoderOffset: Double = 0.0
        set(value){
            field = value
            hardwareDevice.setOffsets(value, yEncoderOffset, DistanceUnit.MM)
        }
    var yEncoderOffset: Double = 0.0
        set(value){
            field = value
            hardwareDevice.setOffsets(xEncoderOffset, value, DistanceUnit.MM)
        }
    var xEncoderDirection: Direction = Direction.FORWARD
        set(value) {
            field = value
            hardwareDevice.setEncoderDirections(
                value.pinpointDir,
                yEncoderDirection.pinpointDir
            )
        }
    var yEncoderDirection: Direction = Direction.FORWARD
        set(value) {
            field = value
            hardwareDevice.setEncoderDirections(
                xEncoderDirection.pinpointDir,
                value.pinpointDir
            )
        }
    var podType: GoBildaOdometryPods = GoBildaOdometryPods.goBILDA_SWINGARM_POD
        set(value) {
            field = value
            hardwareDevice.setEncoderResolution(value)
        }


    override fun resetInternals() {
        hardwareDevice.resetPosAndIMU()
        update(0.0)
        startPos = Pose2D(0, 0, PI / 2)
        update(0.0)
    }
    override fun update(deltaTime: Double) {
        hardwareDevice.update()
        ppPos = hardwareDevice.position.fromSDKPose()
        ppVel = Pose2D(
            hardwareDevice.getVelX(DistanceUnit.INCH),
            hardwareDevice.getVelY(DistanceUnit.INCH),
            hardwareDevice.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS),
        )
        /*
        Logger.recordOutput(
            "Pinpoint/wrappedHeading",
            (hardwareDevice as PinpointInput).device.getHeading(
                AngleUnit.RADIANS
            )
        )
         */
        posBad = (
               ppPos.x.isNaN()
            || ppPos.y.isNaN()
            || ppPos.heading.toDouble().isNaN()
        )
        velBad = (
               ppVel.x.isNaN()
            || ppVel.y.isNaN()
            || ppVel.heading.toDouble().isNaN()
        )
        if(posBad) println("bad position, got a NaN")
        if(velBad) println("bad velocity, got a NaN")

        velocity = (
            if(velBad) velocity
            else ppVel rotatedBy startPos.heading
        )

        position =
            if(posBad) position + ( velocity * deltaTime )
            else ( ppPos rotatedBy startPos.heading ) + startPos

//        position = Pose2D(
//            position.vector,
//            Rotation2D(position.heading.toDouble() % 2 * PI)
//        )
    }

    /**
     * this forces an I/O op to recalibrate IMU
     */
    fun resetHeading() = hardwareDevice.recalibrateIMU()

    fun setStart(value: Pose2D) {
        startPos = value
        hardwareDevice.resetPosAndIMU()
    }

    override fun toString() = "Pinpoint"
}