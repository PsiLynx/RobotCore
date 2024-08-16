package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.ServoController
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction


class FakeCRServo: FakeHardware, CRServo {
    var _power = 0.0
    var _dir = FORWARD

    override fun update(deltaTime: Double) { }
    override fun resetDeviceConfigurationForOpMode() {
        _power = 0.0
        _dir = FORWARD
    }

    override fun setDirection(direction: Direction?) {
        _dir = direction!!
    }
    override fun getDirection() = _dir

    override fun setPower(power: Double) {
        _power = power.coerceIn(-1.0, 1.0)
    }
    override fun getPower() = _power

    override fun getController() = throw NotImplementedError("please don't. why would you even try and do that?")
    override fun getPortNumber() = 0
}