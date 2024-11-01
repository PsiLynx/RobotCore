package org.ftc3825.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.ftc3825.util.rotations
import kotlin.math.PI

class Encoder(
    private val motor: DcMotor,
    val ticksPerRevolution: Double,
    var reversed: Int = 1
    ){

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

    fun update(){
        lastTicks = currentTicks
        currentTicks = motor.currentPosition + 0.0
    }

    fun reset(){
        offsetTicks = - motor.currentPosition + 0.0
    }
}
