package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.math.PI

class Encoder(
    private val motor: DcMotor,
    val ticksPerRevolution: Int,
    var wheelRadius: Double = 0.0,
    var gearRatio: Double = 1.0,
              ) {

    private var traveled: Double = 0.0
    private var angleOffset = 0.0

    private var lastPos = 0.0

    var angle: Double
        get() {
            return (traveled + angleOffset) % 360
        }
        set(newAngle: Double){
            angleOffset = (newAngle - angle)
        }

    var distance: Double
        get() = traveled * wheelRadius * 2 * PI / gearRatio
        set(newDist: Double){
            traveled = newDist * gearRatio / ( wheelRadius * 2 * PI)
            lastPos = traveled
        }
    val delta: Double
        get() = traveled - lastPos
    fun update(){
        lastPos = traveled
        traveled += motor.currentPosition / ticksPerRevolution
    }
}