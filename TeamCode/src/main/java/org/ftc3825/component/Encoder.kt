package org.ftc3825.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareDevice

class Encoder(
    private val motor: DcMotor,
    val ticksPerRevolution: Double,
    var reversed: Int = 1
): Component{

    override var lastWrite = LastWrite.empty()
    override val hardwareDevice: HardwareDevice
        get() = motor

    private var currentTicks: Double = 0.0
    private var lastTicks = 0.0

    private var offsetTicks = 0.0

    var distance: Double
        get() = (currentTicks + offsetTicks) * reversed
        set(newDist){
            offsetTicks += - distance + newDist
        }
    val delta: Double
        get() = (currentTicks - lastTicks) * reversed

    override fun update(deltaTime: Double) {
        lastTicks = currentTicks
        currentTicks = motor.currentPosition + 0.0
    }

    fun resetPosition(){
        offsetTicks = - motor.currentPosition + 0.0
    }

    override fun resetInternals() { }
}
