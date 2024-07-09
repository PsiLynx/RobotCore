package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoController

class FakeServo : FakeHardware(), Servo {
    var _dir = Servo.Direction.FORWARD
    var _pos = 0.0
    var _min = 0.0
    var _max = 0.0
    override fun getController() = throw NotImplementedError("You're in to deep if you need a servo's controller")

    override fun setDirection(p0: Servo.Direction?) {
        _dir = p0!!
    }
    override fun getDirection() = _dir

    override fun setPosition(p0: Double) {
        _pos = p0
    }
    override fun getPosition() = _pos

    override fun scaleRange(min: Double, max: Double) {
        _min = min
        _max = max
    }

    override fun update(deltaTime: Double) {

    }
}
