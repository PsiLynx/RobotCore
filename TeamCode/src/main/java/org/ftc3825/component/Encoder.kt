package org.ftc3825.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.util.rotations
import kotlin.math.PI

class Encoder(
    private val motor: DcMotor,
    val ticksPerRevolution: Double,
    var reversed: Int = 1
    ){

    private var currentTicks: Double = 0.0
    private var revOffset = 0.0

    private var lastTicks = 0.0

    /** angle in radians */
    var angle: Double
        get() = rotations( (currentTicks + revOffset) % 1 )
        set(newAngle){ revOffset = newAngle / ( 2 * PI) }

    var distance: Double
        get() = currentTicks * reversed
        set(newDist){
            currentTicks = newDist * reversed
            lastTicks = currentTicks * reversed
        }
    val delta: Double
        get() = (currentTicks - lastTicks) * ticksPerRevolution * reversed

    fun update(){
        lastTicks = currentTicks
        currentTicks = motor.currentPosition * 1.0
    }
}