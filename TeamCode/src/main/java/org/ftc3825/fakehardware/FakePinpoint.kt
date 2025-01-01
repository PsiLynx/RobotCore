package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import org.ftc3825.pedroPathing.localization.GoBildaPinpointDriver
import org.ftc3825.sim.maxDriveVelocity
import org.ftc3825.sim.maxStrafeVelocity
import org.ftc3825.sim.maxTurnVelocity
import org.ftc3825.sim.timeStep
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName

class FakePinpoint: GoBildaPinpointDriver(FakeI2cDeviceSynchSimple(), false) {
    val fl = FakeHardwareMap.get(DcMotor::class.java, flMotorName) as FakeMotor
    val fr = FakeHardwareMap.get(DcMotor::class.java, frMotorName) as FakeMotor
    val bl = FakeHardwareMap.get(DcMotor::class.java, blMotorName) as FakeMotor
    val br = FakeHardwareMap.get(DcMotor::class.java, brMotorName) as FakeMotor

    var _pos = Pose2D(DistanceUnit.INCH, 0.0, 0.0, AngleUnit.RADIANS, 0.0)

    override fun update() {
        val drive  = (fl.speed + fr.speed + bl.speed + br.speed) / 4
        val strafe = (bl.speed + fr.speed - bl.speed - br.speed) / 4
        val turn   = (fr.speed + br.speed + fl.speed + bl.speed) / 4
        _pos = Pose2D(
            DistanceUnit.INCH,
            _pos.getX(DistanceUnit.INCH) + strafe * timeStep * maxStrafeVelocity,
            _pos.getY(DistanceUnit.INCH) + drive * timeStep * maxDriveVelocity,
            AngleUnit.RADIANS,
            _pos.getHeading(AngleUnit.RADIANS) + turn * timeStep * maxTurnVelocity,
        )
    }
    override fun resetPosAndIMU() {
        _pos = Pose2D(DistanceUnit.INCH, 0.0, 0.0, AngleUnit.RADIANS, 0.0)
    }
    override fun getPosition() = _pos

    override fun setOffsets(xOffset: Double, yOffset: Double) { }
    override fun setEncoderDirections(xEncoder: EncoderDirection?, yEncoder: EncoderDirection?) { }
    override fun setEncoderResolution(pods: GoBildaOdometryPods?) { }
}