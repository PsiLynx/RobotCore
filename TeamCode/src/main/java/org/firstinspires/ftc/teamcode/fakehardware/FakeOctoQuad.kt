package org.firstinspires.ftc.teamcode.fakehardware

import org.firstinspires.ftc.teamcode.OctoQuadFWv3
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.DeviceTimes
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxDriveVelocity
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxStrafeVelocity
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxTurnVelocity
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import kotlin.math.PI
import kotlin.reflect.jvm.isAccessible

class FakeOctoQuad (): OctoQuadFWv3(FakeI2cDeviceSynchSimple(), false) {


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
    var lastPos = _pos

    override fun setAllLocalizerParameters(
        portX: Int,
        portY: Int,
        ticksPerMM_x: Float,
        ticksPerMM_y: Float,
        tcpOffsetMM_X: Float,
        tcpOffsetMM_Y: Float,
        headingScalar: Float,
        velocityIntervalMs: Int
    ){}
    override fun setSingleEncoderDirection(
        idx: Int,
        direction: Component.Direction?
    ){}
    override fun resetLocalizerAndCalibrateIMU(){
        _pos = Pose2D(0.0, 0.0, PI/2)
    }

    override fun readLocalizerData(): LocalizerDataBlock? {
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

        val data = LocalizerDataBlock()
        data.position = _pos
        data.crcOk = true
        data.velocity = _pos - lastPos // TODO: FIX
        return data
    }
}