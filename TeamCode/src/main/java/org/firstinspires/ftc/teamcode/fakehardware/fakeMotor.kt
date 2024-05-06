package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorController
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType

class fakeMotor: DcMotor {
    override fun getManufacturer(): HardwareDevice.Manufacturer {
        return HardwareDevice.Manufacturer.Other
    }

    override fun getDeviceName(): String {
        return ""
    }

    override fun getConnectionInfo(): String {
        return ""
    }

    override fun getVersion(): Int {
        return 0
    }

    override fun resetDeviceConfigurationForOpMode() {

    }

    override fun close() {

    }

    override fun setDirection(p0: DcMotorSimple.Direction?) {

    }

    override fun getDirection(): DcMotorSimple.Direction {
        return DcMotorSimple.Direction.FORWARD
    }

    override fun setPower(p0: Double) {

    }

    override fun getPower(): Double {
        return 0.0
    }

    override fun getMotorType(): MotorConfigurationType {
        return MotorConfigurationType()
    }

    override fun setMotorType(p0: MotorConfigurationType?) {

    }

    override fun getController(): DcMotorController {
        TODO()
    }

    override fun getPortNumber(): Int {
        return 0
    }

    override fun setZeroPowerBehavior(p0: DcMotor.ZeroPowerBehavior?) {

    }

    override fun getZeroPowerBehavior(): DcMotor.ZeroPowerBehavior {
        return DcMotor.ZeroPowerBehavior.UNKNOWN
    }

    @Deprecated("Deprecated in Java")
    override fun setPowerFloat() {

    }

    override fun getPowerFloat(): Boolean {
        return false
    }

    override fun setTargetPosition(p0: Int) {

    }

    override fun getTargetPosition(): Int {
        return 0
    }

    override fun isBusy(): Boolean {
        return false
    }

    override fun getCurrentPosition(): Int {
        return 0
    }

    override fun setMode(p0: DcMotor.RunMode?) {

    }

    override fun getMode(): DcMotor.RunMode {
        return DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }
}