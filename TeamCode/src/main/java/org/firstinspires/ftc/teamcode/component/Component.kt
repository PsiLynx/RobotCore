package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareDevice

interface Component {
    var lastWrite: LastWrite
    val hardwareDevice: HardwareDevice

    fun resetInternals()
    fun update(deltaTime: Double = 0.0)

    fun reset() {
        lastWrite = LastWrite.empty()
        hardwareDevice.resetDeviceConfigurationForOpMode()
        resetInternals()
    }
    enum class Direction(val dir: Int){
        FORWARD(1), REVERSE(-1)
    }
}