package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareDevice

abstract class Component: Comparable<Component> {
    abstract val hardwareDevice: HardwareDevice

    abstract val priority: Double
    abstract val ioOpTimeMs: Double

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
        const val motor    = 1.0
        const val servo    = 1.0
        const val crServo  = 1.0
        const val pinpoint = 1.0
        const val octoQuad = 2.4
    }
}