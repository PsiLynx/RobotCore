package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.DeviceTimes
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxDriveVelocity
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxStrafeVelocity
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxTurnVelocity
import org.firstinspires.ftc.teamcode.util.geometry.SDKPose
import org.firstinspires.ftc.teamcode.util.geometry.fromSDKPose
import kotlin.Double.Companion.NaN
import kotlin.random.Random


class FakePinpoint: GoBildaPinpointDriver(FakeI2cDeviceSynchSimple(), false) {
    private val fl =
        HardwareMap.frontLeft(Component.Direction.FORWARD).hardwareDevice
        as FakeMotor
    private val fr =
        HardwareMap.frontRight(Component.Direction.FORWARD).hardwareDevice
        as FakeMotor
    private val bl =
        HardwareMap.backLeft(Component.Direction.FORWARD).hardwareDevice
        as FakeMotor
    private val br =
        HardwareMap.backRight(Component.Direction.FORWARD).hardwareDevice
        as FakeMotor

    var chanceOfNaN = 0.0

    var _pos = Pose2D(0.0, 0.0, 0.0)
    private var lastPos = _pos

    override fun update() {
        val flSpeed =   fl.speed
        val blSpeed =   bl.speed
        val frSpeed = - fr.speed
        val brSpeed = - br.speed
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

    override fun getVelX(unit: DistanceUnit) = unit.fromInches(_pos.x)
    override fun getVelY(unit: DistanceUnit) = unit.fromInches(_pos.y)

    override fun getHeadingVelocity(unit: UnnormalizedAngleUnit)
        = unit .fromRadians(_pos.heading.toDouble())

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
}