package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.DeviceTimes
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxDriveVelocity
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxStrafeVelocity
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxTurnVelocity
import org.firstinspires.ftc.teamcode.geometry.SDKPose
import org.firstinspires.ftc.teamcode.geometry.fromSDKPose
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import kotlin.Double.Companion.NaN
import kotlin.random.Random
import kotlin.reflect.jvm.isAccessible


class FakePinpoint: GoBildaPinpointDriver(FakeI2cDeviceSynchSimple(), false) {
    private val fl =
        HardwareMap.frontLeft(Component.Direction.FORWARD).hardwareDevice
        as MotorWrapper
    private val fr =
        HardwareMap.frontRight(Component.Direction.FORWARD).hardwareDevice
        as MotorWrapper
    private val bl =
        HardwareMap.backLeft(Component.Direction.FORWARD).hardwareDevice
        as MotorWrapper
    private val br =
        HardwareMap.backRight(Component.Direction.FORWARD).hardwareDevice
        as MotorWrapper

    var chanceOfNaN = 0.0

    var _pos = Pose2D(0.0, 0.0, 0.0)
    private var lastPos = _pos

    override fun update() {
        val field = fl::class.members.first { it.name == "device" }
        field.isAccessible = true

        val flSpeed =   ( field.call(fl) as FakeMotor ).speed
        val blSpeed =   ( field.call(bl) as FakeMotor ).speed
        val frSpeed = - ( field.call(fr) as FakeMotor ).speed
        val brSpeed = - ( field.call(br) as FakeMotor ).speed

        val drive  = ( flSpeed + frSpeed + blSpeed + brSpeed ) / 4
        val strafe = ( blSpeed + frSpeed - flSpeed - brSpeed ) / 4
        val turn   = ( brSpeed + frSpeed - flSpeed - blSpeed ) / 4
        lastPos = _pos
        val offset = Pose2D(
            drive  * CommandScheduler.deltaTime * maxDriveVelocity,
            strafe * CommandScheduler.deltaTime * maxStrafeVelocity,
            turn   * CommandScheduler.deltaTime * maxTurnVelocity,
        )
        _pos += (offset rotatedBy _pos.heading)
        FakeTimer.addTime(DeviceTimes.pinpoint)
    }
    override fun resetPosAndIMU() {
        _pos = Pose2D(0.0, 0.0, 0.0)
    }
    override fun getPosition() = (
        if(Random.nextDouble() < chanceOfNaN) Pose2D(NaN, NaN, NaN)
        else _pos
    ).asSDKPose()

    override fun setPosition(pos: SDKPose) {
        _pos = pos.fromSDKPose()
    }

    override fun getVelX(unit: DistanceUnit)
        = unit.fromInches((_pos - lastPos).x) / CommandScheduler.deltaTime
    override fun getVelY(unit: DistanceUnit)
        = unit.fromInches((_pos - lastPos).y) / CommandScheduler.deltaTime

    override fun getHeadingVelocity(unit: UnnormalizedAngleUnit)
        = unit.fromRadians((_pos - lastPos).heading.toDouble()) / CommandScheduler.deltaTime

    override fun setOffsets(
        xOffset: Double,
        yOffset: Double,
        unit: DistanceUnit
    ) { }
    override fun setEncoderDirections(
        xEncoder: EncoderDirection,
        yEncoder: EncoderDirection,
    ) { }
    override fun setEncoderResolution(pods: GoBildaOdometryPods?) { }

    override fun getDeviceID(): Int {
        return 1
    }
    override fun getDeviceVersion(): Int {
        return 1
    }
    override fun getYawScalar(): Float {
        return 1.0f
    }
    override fun getDeviceStatus(): DeviceStatus {
        return DeviceStatus.READY
    }
    override fun getLoopTime(): Int {
        return 600
    }
    override fun getFrequency(): Double {
        return if (loopTime != 0) {
            1000000.0 / loopTime;
        } else {
            0.0;
        }
    }
    override fun getEncoderX(): Int {
        return 0
    }
    override fun getEncoderY(): Int {
        return 0
    }
    override fun getPosX(distanceUnit: DistanceUnit): Double {
        return distanceUnit.fromInches(_pos.x)
    }
    override fun getPosY(distanceUnit: DistanceUnit): Double {
        return distanceUnit.fromInches(_pos.y)
    }
    override fun getHeading(angleUnit: AngleUnit): Double {
        return angleUnit.fromRadians(_pos.heading.toDouble())
    }
    override fun getHeading(unnormalizedAngleUnit: UnnormalizedAngleUnit): Double {
        return unnormalizedAngleUnit.fromRadians(_pos.heading.toDouble())
    }

    override fun getXOffset(distanceUnit: DistanceUnit): Float {
        return 0.0f
    }
    override fun getYOffset(distanceUnit: DistanceUnit): Float {
        return 0.0f
    }

}