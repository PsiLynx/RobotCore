package org.firstinspires.ftc.teamcode.component

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.HardwareDevice

abstract class Component {
    abstract val hardwareDevice: HardwareDevice

    abstract fun resetInternals()
    abstract fun update(deltaTime: Double)

    fun reset() {
        hardwareDevice.resetDeviceConfigurationForOpMode()
        resetInternals()
    }

    enum class Direction(
        val dir: Int,
    ){
        FORWARD( 1),
        REVERSE(-1)
    }
    enum class Opening{
        OPEN, CLOSED
    }
    val Direction.pinpointDir: GoBildaPinpointDriver.EncoderDirection
        get() = GoBildaPinpointDriver.EncoderDirection.entries[ordinal]
}