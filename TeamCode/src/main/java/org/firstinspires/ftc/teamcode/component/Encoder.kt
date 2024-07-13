package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.util.rotations
import kotlin.math.PI

class Encoder(
    private val motor: DcMotor,
    val ticksPerRevolution: Double,
    var wheelRadius: Double = 0.0,
    private var gearRatio: Double = 1.0,
    ){

    private var revolutions: Double = 0.0
    private var revOffset = 0.0

    private var lastPos = 0.0

    /** angle in radians */
    var angle: Double
        get() = rotations( (revolutions + revOffset) % 1 )
        set(newAngle){ revOffset = newAngle / ( 2 * PI) }

    var distance: Double
        get() = revolutions * wheelRadius * 2 * PI / gearRatio
        set(newDist){
            revolutions = newDist * gearRatio / ( wheelRadius * 2 * PI)
            lastPos = revolutions
        }
    val delta: Double
        get() = revolutions - lastPos
    fun update(){
        lastPos = revolutions
        revolutions = motor.currentPosition / ticksPerRevolution
    }
}