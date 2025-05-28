package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareDevice

abstract class Component: Comparable<Component>{
    abstract val hardwareDevice: HardwareDevice

    abstract var priority: Double
    abstract val ioOpTime: Double

    abstract fun resetInternals()
    abstract fun update(deltaTime: Double)

    fun reset() {
        hardwareDevice.resetDeviceConfigurationForOpMode()
        resetInternals()
    }
    abstract fun ioOp()

    override fun compareTo(other: Component)
        = ( (this.priority - other.priority) * 1000 ).toInt()

    enum class Direction(val dir: Int){
        FORWARD(1), REVERSE(-1)
    }
}