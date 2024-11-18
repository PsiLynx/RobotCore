package org.ftc3825.component

import com.qualcomm.robotcore.hardware.HardwareDevice

interface Component {
    var lastWrite: Double?
    val hardwareDevice: HardwareDevice

    fun resetInternals()
    fun update(deltaTime: Double = 0.0)

    fun reset() {
        lastWrite = null
        hardwareDevice.resetDeviceConfigurationForOpMode()
    }
}