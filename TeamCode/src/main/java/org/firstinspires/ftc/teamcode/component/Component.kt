package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.teamcode.util.millis

abstract class Component: Comparable<Component> {
    abstract val hardwareDevice: HardwareDevice

    abstract val priority: Double
    abstract val ioOpTime: Double

    abstract fun resetInternals()
    abstract fun update(deltaTime: Double = 0.0)
    abstract fun ioOp()

    override fun compareTo(other: Component)
        = ( (this.priority - other.priority) * 1000 ).toInt()

    fun reset() {
        hardwareDevice.resetDeviceConfigurationForOpMode()
        resetInternals()
    }
    enum class Direction(val dir: Int){
        FORWARD(1), REVERSE(-1)
    }
    object DeviceTimes {
        val motor    = millis(1.0)
        val servo    = millis(1.0)
        val crServo  = millis(1.0)
        val pinpoint = millis(1.0)
        val octoQuad = millis(2.4)
    }
}