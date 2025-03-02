package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.GoBildaPinpointDriver
import org.ftc3825.sim.SimConstants.maxDriveVelocity
import org.ftc3825.sim.SimConstants.maxStrafeVelocity
import org.ftc3825.sim.SimConstants.maxTurnVelocity
import org.ftc3825.sim.SimConstants.timeStep
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName


class FakePinpoint: GoBildaPinpointDriver(FakeI2cDeviceSynchSimple(), false) {
    private val fl =
        FakeHardwareMap.get(DcMotor::class.java, flMotorName) as FakeMotor
    private val fr =
        FakeHardwareMap.get(DcMotor::class.java, frMotorName) as FakeMotor
    private val bl =
        FakeHardwareMap.get(DcMotor::class.java, blMotorName) as FakeMotor
    private val br =
        FakeHardwareMap.get(DcMotor::class.java, brMotorName) as FakeMotor

    var _pos = Pose2D(0.0, 0.0, 0.0)
    private var lastPos = _pos

    override fun update() {
        val flSpeed =   fl.speed
        val blSpeed =   bl.speed
        val frSpeed = - fr.speed
        val brSpeed = - br.speed
        val drive  = (flSpeed + frSpeed + blSpeed + brSpeed) / 4
        val strafe = ( blSpeed + frSpeed - flSpeed - brSpeed) / 4
        val turn   = (brSpeed + frSpeed - flSpeed - blSpeed) / 4
        lastPos = _pos
        val offset = Pose2D(
            drive * timeStep * maxDriveVelocity,
            strafe * timeStep * maxStrafeVelocity,
            turn * timeStep * maxTurnVelocity,
        )
        _pos += (offset rotatedBy _pos.heading)
    }
    override fun resetPosAndIMU() {
        _pos = Pose2D(0.0, 0.0, 0.0)
    }
    override fun getPosition() = _pos
    override fun setPosition(pos: Pose2D?): Pose2D {
        _pos = pos!!
        return _pos
    }

    override fun getVelocity() = _pos - lastPos

    override fun setOffsets(xOffset: Double, yOffset: Double) { }
    override fun setEncoderDirections(xEncoder: EncoderDirection?, yEncoder: EncoderDirection?) { }
    override fun setEncoderResolution(pods: GoBildaOdometryPods?) { }
}