package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.HardwareDevice

interface FakeHardware:HardwareDevice {
    override fun getManufacturer() = HardwareDevice.Manufacturer.Other
    override fun getDeviceName() = ""
    override fun getConnectionInfo() =  ""
    override fun getVersion() = 0
    override fun resetDeviceConfigurationForOpMode() { }
    override fun close() { }

    fun getPortNumber() = 0
    fun update(deltaTime: Double)
}