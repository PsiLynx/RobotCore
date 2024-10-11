package org.ftc3825.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.util.rotations
import kotlin.math.PI

class Encoder(
    private val motor: DcMotor,
    val ticksPerRevolution: Double,
    var wheelRadius: Double = 0.0,
    private var gearRatio: Double = 1.0,
    ){

    private var currentTicks: Double = 0.0
    private var revOffset = 0.0

    private var lastTicks = 0.0

    /** angle in radians */
    var angle: Double
        get() = rotations( (currentTicks + revOffset) % 1 )
        set(newAngle){ revOffset = newAngle / ( 2 * PI) }

    var distance: Double
        get() = currentTicks
        set(newDist){
            currentTicks = newDist
            lastTicks = currentTicks
        }
    val delta: Double
        get() = (currentTicks - lastTicks) * ticksPerRevolution

    fun update(){
        lastTicks = currentTicks
        currentTicks += motor.currentPosition - lastTicks
    }

    fun reset(){
        lastTicks = 0.0
        currentTicks = 0.0
    }
}
