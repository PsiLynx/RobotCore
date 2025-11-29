package org.firstinspires.ftc.teamcode.fakehardware

import android.R.attr.direction
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT
import com.qualcomm.robotcore.hardware.DcMotorControllerEx
import com.qualcomm.robotcore.hardware.DcMotorImplEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.DeviceTimes
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import kotlin.math.abs

open class FakeMotor: FakeHardware, DcMotorImplEx(
    object : DcMotorControllerEx {
        override fun setMotorType(motor: Int, motorType: MotorConfigurationType?) {}
        override fun getMotorType(motor: Int) = MotorConfigurationType.getUnspecifiedMotorType()
        override fun setMotorMode(motor: Int, mode: DcMotor.RunMode?) {}
        override fun getMotorMode(motor: Int) = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        override fun setMotorPower(motor: Int, power: Double) {}
        override fun getMotorPower(motor: Int) = 0.0
        override fun isBusy(motor: Int) = false
        override fun setMotorZeroPowerBehavior(motor: Int, zeroPowerBehavior: DcMotor.ZeroPowerBehavior?) {}
        override fun getMotorZeroPowerBehavior(motor: Int) = DcMotor.ZeroPowerBehavior.UNKNOWN
        override fun getMotorPowerFloat(motor: Int) = false
        override fun setMotorTargetPosition(motor: Int, position: Int) {}
        override fun getMotorTargetPosition(motor: Int) = 0
        override fun getMotorCurrentPosition(motor: Int) = 0
        override fun resetDeviceConfigurationForOpMode(motor: Int) {}
        override fun getManufacturer() = HardwareDevice.Manufacturer.Other
        override fun getDeviceName() = "MockMotor"
        override fun getConnectionInfo() = ""
        override fun getVersion() = 1
        override fun resetDeviceConfigurationForOpMode() {}
        override fun close() {}
        override fun setMotorEnable(motor: Int) {}
        override fun setMotorDisable(motor: Int) {}
        override fun isMotorEnabled(motor: Int) = false
        override fun setMotorVelocity(motor: Int, ticksPerSecond: Double) {}
        override fun setMotorVelocity(motor: Int, angularRate: Double, unit: AngleUnit?) {}
        override fun getMotorVelocity(motor: Int) = 0.0
        override fun getMotorVelocity(motor: Int, unit: AngleUnit?) = 0.0
        override fun setPIDCoefficients(motor: Int, mode: DcMotor.RunMode?, pidCoefficients: PIDCoefficients?) {}
        override fun setPIDFCoefficients(motor: Int, mode: DcMotor.RunMode?, pidfCoefficients: PIDFCoefficients?) {}
        override fun getPIDCoefficients(motor: Int, mode: DcMotor.RunMode?) = PIDCoefficients()
        override fun getPIDFCoefficients(motor: Int, mode: DcMotor.RunMode?) = PIDFCoefficients()
        override fun setMotorTargetPosition(motor: Int, position: Int, tolerance: Int) {}
        override fun getMotorCurrent(motor: Int, unit: CurrentUnit?) = 0.0
        override fun getMotorCurrentAlert(motor: Int, unit: CurrentUnit?) = 0.0
        override fun setMotorCurrentAlert(motor: Int, current: Double, unit: CurrentUnit?) {}
        override fun isMotorOverCurrent(motor: Int) = false
    },
    0,
    FORWARD,
    MotorConfigurationType()

) {

    private var _power = 0.0
    private var _pos = 0.0
    private var _direction = FORWARD
    private var _zeroPowerBehavior = FLOAT

    open var maxVelocityInTicksPerSecond = 500
    open var maxAccel = 16
    var speed: Double = 0.0
        internal set

    override fun update(deltaTime: Double) {
        speed += ( _power - speed ) * maxAccel * deltaTime

        if (abs(speed) < 0.02 && abs(_power) < 0.02) speed = 0.0

        updatePosition(deltaTime)
    }

    private fun updatePosition(deltaTime: Double) {
        _pos += (speed * maxVelocityInTicksPerSecond * deltaTime)
    }

    override fun getManufacturer() = super<FakeHardware>.getManufacturer()

    override fun getDeviceName() = super<FakeHardware>.getDeviceName()

    override fun getConnectionInfo() = super<FakeHardware>.getConnectionInfo()

    override fun getVersion() = super<FakeHardware>.getVersion()

    override fun resetDeviceConfigurationForOpMode() {
         zeroPowerBehavior = FLOAT
         direction         = FORWARD
        _pos               = 0.0
        _power             = 0.0
         speed             = 0.0
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun getDirection() = _direction
    override fun setDirection(p0: DcMotorSimple.Direction?) { _direction = p0!!}

    override fun getPower() = _power
    override fun setPower(p0: Double) {
        _power = p0.coerceIn(-1.0, 1.0)
        FakeTimer.addTime(DeviceTimes.chubMotor) //TODO: setup correctly
    }

    override fun getZeroPowerBehavior() = _zeroPowerBehavior
    override fun setZeroPowerBehavior(p0: DcMotor.ZeroPowerBehavior?) { _zeroPowerBehavior = p0!!}

    override fun getCurrentPosition() = _pos.toInt()

    open fun setCurrentPosition(newPos: Number){ _pos = newPos.toDouble() }

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
    override fun getController() = TODO( "You're in too deep if you need the hardwareDevice's controller" )
    override fun getPortNumber() = 0

}
