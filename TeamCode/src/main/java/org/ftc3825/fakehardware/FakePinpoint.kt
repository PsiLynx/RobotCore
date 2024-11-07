package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import org.ftc3825.util.GoBildaPinpointDriver

class FakePinpoint: GoBildaPinpointDriver(FakeI2cDeviceSynchSimple(), false) {
    override fun update() { }
    override fun resetPosAndIMU() { }
    override fun setOffsets(xOffset: Double, yOffset: Double) { }
    override fun setEncoderDirections(xEncoder: EncoderDirection?, yEncoder: EncoderDirection?) { }
    override fun setEncoderResolution(pods: GoBildaOdometryPods?) { }

    override fun getPosition() = Pose2D(DistanceUnit.INCH, 0.0, 0.0, AngleUnit.RADIANS, 0.0)
}