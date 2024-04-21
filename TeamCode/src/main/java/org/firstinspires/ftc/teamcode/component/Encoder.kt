package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.math.PI

class Encoder(
    private val motor: DcMotor,
    val ticksPerRevolution: Double,
    var wheelRadius: Double = 0.0,
    var gearRatio: Double = 1.0,
              ) {

    private var revolutions: Double = 0.0
    private var angleOffset = 0.0

    private var lastPos = 0.0

    var angle: Double
        get() {
            return ( (revolutions + angleOffset) % 1 ) * 2 * PI
        }
        set(newAngle: Double){
            angleOffset = (newAngle - angle) / ( 2 * PI)
        }

    var distance: Double
        get() = revolutions * wheelRadius * 2 * PI / gearRatio
        set(newDist: Double){
            revolutions = newDist * gearRatio / ( wheelRadius * 2 * PI)
            lastPos = revolutions
        }
    val delta: Double
        get() = revolutions - lastPos
    fun update(){
        lastPos = revolutions
        revolutions += motor.currentPosition / ticksPerRevolution
    }
}