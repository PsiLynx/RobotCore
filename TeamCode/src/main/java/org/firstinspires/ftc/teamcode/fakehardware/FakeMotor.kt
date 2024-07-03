package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import kotlin.math.abs

class FakeMotor: DcMotor, FakeHardware() {
    private var _power = 0.0
    private var _pos = 0
    private var _direction = FORWARD
    private var _zeroPowerBehavior = FLOAT

    var maxAccel = 0.05
    var speed: Double = 0.0
        private set(newSpeed: Double) {
            field = newSpeed
        }
    override fun update() {
        speed /= (maxAccel + 1)
        speed += ( power - speed ) * maxAccel

        if(abs(speed) < 0.04) speed = 0.0
    }
    override fun setDirection(p0: DcMotorSimple.Direction?) { _direction = p0!!}
    override fun getDirection() = _direction
    override fun setPower(p0: Double) {
        _power = ( p0.let {if (it > 1.0) 1.0 else if (it < -1.0) -1.0 else it} )
    }
    override fun getPower() = _power
    override fun setZeroPowerBehavior(p0: DcMotor.ZeroPowerBehavior?) { _zeroPowerBehavior = p0!!}
    override fun getZeroPowerBehavior() = _zeroPowerBehavior
    override fun getCurrentPosition() = _pos
    fun setCurrentPosition(newPos:Int){
        _pos = newPos
    }
    override fun resetDeviceConfigurationForOpMode() {
        zeroPowerBehavior = FLOAT
        direction = FORWARD
        currentPosition = 0
        power = 0.0
    }
    // ==== dummy methods ====
    @Deprecated("Deprecated in Java")
    override fun setPowerFloat() { }
    override fun getPowerFloat() = false
    override fun setTargetPosition(p0: Int) { }
    override fun getTargetPosition() = 0
    override fun isBusy() = false
    override fun setMode(p0: DcMotor.RunMode?) { }
    override fun getMode() = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    override fun getMotorType() = MotorConfigurationType()
    override fun setMotorType(p0: MotorConfigurationType?) { }
    override fun getController() = TODO( ":(" )
    override fun getPortNumber() = 0

}