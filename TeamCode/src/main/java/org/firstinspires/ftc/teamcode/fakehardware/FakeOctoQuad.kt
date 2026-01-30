package org.firstinspires.ftc.teamcode.fakehardware

import org.firstinspires.ftc.teamcode.OctoQuadFWv3
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.DeviceTimes
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxDriveVelocity
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxStrafeVelocity
import org.firstinspires.ftc.teamcode.sim.SimConstants.maxTurnVelocity
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import kotlin.math.PI
import kotlin.reflect.jvm.isAccessible

class FakeOctoQuad (): OctoQuadFWv3(FakeI2cDeviceSynchSimple(), false) {


    private val fl =
        HardwareMap.frontLeft(Component.Direction.FORWARD).hardwareDevice
                //as MotorWrapper
    private val fr =
        HardwareMap.frontRight(Component.Direction.FORWARD).hardwareDevice
                //as MotorWrapper
    private val bl =
        HardwareMap.backLeft(Component.Direction.FORWARD).hardwareDevice
                //as MotorWrapper
    private val br =
        HardwareMap.backRight(Component.Direction.FORWARD).hardwareDevice
                //as MotorWrapper

    var chanceOfNaN = 0.0

    var _pos = Pose2D(0.0, 0.0, 0.0)
    var lastPos = _pos
    var lastTime = Globals.currentTime

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
        _pos = Pose2D(0.0, 0.0, 0.0)
    }

    override fun readLocalizerData(): LocalizerDataBlock? {
        //val field = fl::class.members.first { it.name == "device" }
        //field.isAccessible = true

        /*
        val flSpeed =   ( field.call(fl) as FakeMotor ).speed
        val blSpeed =   ( field.call(bl) as FakeMotor ).speed
        val frSpeed = - ( field.call(fr) as FakeMotor ).speed
        val brSpeed = - ( field.call(br) as FakeMotor ).speed
         */
        val flSpeed =   ( fl as FakeMotor ).speed
        val blSpeed =   ( bl as FakeMotor ).speed
        val frSpeed = - ( fr as FakeMotor ).speed
        val brSpeed = - ( br as FakeMotor ).speed

        val drive  = ( flSpeed + frSpeed + blSpeed + brSpeed ) / 4
        val turn   = ( brSpeed + frSpeed - flSpeed - blSpeed ) / 4
        lastPos = _pos
        val deltaTime = Globals.currentTime - lastTime
        lastTime = Globals.currentTime
        val offset = Pose2D(
            drive  * deltaTime * maxDriveVelocity,
            0,
            turn   * deltaTime * maxTurnVelocity,
        )
        _pos += (offset rotatedBy _pos.heading)
        log("pos") value _pos
        log("last pos") value lastPos
        FakeTimer.addTime(DeviceTimes.octoquad)

        val data = LocalizerDataBlock()
        data.position = _pos
        data.crcOk = true
        data.velocity = (_pos - lastPos) / deltaTime
        log("vel") value data.velocity
        log("robot centric vel") value ChassisSpeeds(
            vx = 0.0,
            vy = data.velocity.mag,
            vTheta = data.velocity.heading.toDouble()
        )
        log("delta time") value deltaTime
        return data
    }
}