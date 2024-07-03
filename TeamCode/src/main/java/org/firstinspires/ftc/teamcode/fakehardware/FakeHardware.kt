package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.HardwareDevice

abstract class FakeHardware:HardwareDevice {
    open override fun getManufacturer() = HardwareDevice.Manufacturer.Other
    open override fun getDeviceName() = ""
    open override fun getConnectionInfo() =  ""
    open override fun getVersion() = 0
    open override fun resetDeviceConfigurationForOpMode() { }
    open override fun close() { }

    open fun getPortNumber() = 0
    open fun update() { }
}