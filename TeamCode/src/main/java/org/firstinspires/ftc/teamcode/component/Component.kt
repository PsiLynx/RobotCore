package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareDevice

interface Component {
    val hardwareDevice: HardwareDevice

    fun resetInternals()
    fun update(deltaTime: Double)

    fun reset() {
        hardwareDevice.resetDeviceConfigurationForOpMode()
        resetInternals()
    }
    enum class Direction(val dir: Int){
        FORWARD(1), REVERSE(-1)
    }
}