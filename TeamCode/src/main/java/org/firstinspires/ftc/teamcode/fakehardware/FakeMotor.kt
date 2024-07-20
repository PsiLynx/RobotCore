package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import org.firstinspires.ftc.teamcode.util.isWithin
import org.firstinspires.ftc.teamcode.util.of

open class FakeMotor: FakeHardware, DcMotor {
    private var _power = 0.0
    private var _pos = 0.0
    private var _direction = FORWARD
    private var _zeroPowerBehavior = FLOAT

    open var maxVelocityInTicksPerSecond = 5000
    var maxAccel = 1
    var speed: Double = 0.0
        internal set

    override fun update(deltaTime: Double) {
        if(power isWithin 0.01 of 0){ speed += -speed * maxAccel * deltaTime }
        else { speed += ( power - speed ) * maxAccel * deltaTime }


        updatePosition(deltaTime)
    }

    protected fun updatePosition(deltaTime: Double) {
        _pos += (speed * maxVelocityInTicksPerSecond * deltaTime)
    }

    override fun resetDeviceConfigurationForOpMode() {
         zeroPowerBehavior = FLOAT
         direction         = FORWARD
        _pos               = 0.0
        _power             = 0.0
         speed             = 0.0
    }

    override fun getDirection() = _direction
    override fun setDirection(p0: DcMotorSimple.Direction?) { _direction = p0!!}

    override fun getPower() = _power
    override fun setPower(p0: Double) { _power = p0.coerceIn(-1.0, 1.0) }

    override fun getZeroPowerBehavior() = _zeroPowerBehavior
    override fun setZeroPowerBehavior(p0: DcMotor.ZeroPowerBehavior?) { _zeroPowerBehavior = p0!!}

    override fun getCurrentPosition() = _pos.toInt()
    fun setCurrentPosition(newPos:Int){ _pos = newPos.toDouble() }

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
    override fun getController() = TODO( "You're in too deep if you need the motor's controller" )
    override fun getPortNumber() = 0

}