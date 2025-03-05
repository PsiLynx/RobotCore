package org.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.Servo

class FakeServo : FakeHardware, Servo {
    private var _dir = Servo.Direction.FORWARD
    private var _pos = 0.0
    private var _min = 0.0
    private var _max = 0.0

    override fun getDirection() = _dir
    override fun setDirection(p0: Servo.Direction?) { _dir = p0!! }

    override fun getPosition() = _pos
    override fun setPosition(p0: Double) { _pos = p0 }

    override fun scaleRange(min: Double, max: Double) {
        _min = min
        _max = max
    }

    override fun update(deltaTime: Double) { }
    override fun resetDeviceConfigurationForOpMode() {
        _dir = Servo.Direction.FORWARD
        _pos = 0.0
        _min = 0.0
        _max = 0.0
    }

    override fun getController() = throw NotImplementedError("You're in to deep if you need a servo's controller")
    override fun getPortNumber() = 0
}
