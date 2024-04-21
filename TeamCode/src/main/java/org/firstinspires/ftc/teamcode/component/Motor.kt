package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.math.abs

class Motor(
    val name: String,
    val hardwareMap: HardwareMap,
    val rpm: Int,
    var gearRatio: Double = 1.0,
    var Kstatic: Double = 0.0,
    var wheelRadius: Double = 1.0,
    val direction: Int = 1
) {
    val motor: DcMotor
    val lastWrite: Double = 0.0
    var ticksPerRev: Double = 1.0
    var encoder: Encoder? = null

    init {
        ticksPerRev = 28 * 6000.0 / rpm //Nevrest motors have 6,000 rpm base and 28 ticks per revolution
        motor = hardwareMap.get(DcMotor::class.java, name)
    }


    fun useInternalEncoder() {
        encoder = Encoder(motor, ticksPerRev, wheelRadius =wheelRadius)
    }

    /**
     * position of the motor in inches.
     * actually a wrapper for encoder.distance.
     * if encoder == null, return 0.0
     */
    var positsion: Double
        get():Double{
            return encoder?.distance ?: 0.0
        }
        set(newPosition: Double):Unit{
            if(!(encoder is Encoder) ) return
            encoder!!.distance = newPosition
        }

    /**
     * angle of the motor in degrees.
     * actually a wrapper for encoder.angle.
     * if encoder == null, return 0.0
     */
    var angle: Double
        get():Double{
            return encoder?.angle ?: 0.0
        }
        set(newPosition: Double):Unit{
            if(!(encoder is Encoder) ) return
            encoder!!.distance = newPosition
        }

    fun setZeroPowerBehavior(behavior: Int) {
        motor.zeroPowerBehavior = zeroPowerBehaviors[behavior]
    }

    fun setDirection(direction: Int) {
        when (direction) {
            FORWARD -> {
                run { motor.direction = DcMotorSimple.Direction.FORWARD }
            }

            REVERSE -> {
                run { motor.direction = DcMotorSimple.Direction.REVERSE }
            }
        }
    }

    fun setPower(speed: Double) {
        var speed = speed
        if (abs(speed - lastWrite) < EPSILON) {
            return
        }
        speed = (1 - Kstatic) * speed + Kstatic //lerp from Kstatic to 1
        motor.power = speed
    }

    companion object {
        const val EPSILON = 0.005 //less than this and you don't write to the motors
        val BRAKE = 0
        val FLOAT = 1
        val UNKNOWN = 2
        const val FORWARD = 1
        const val REVERSE = -1
        val zeroPowerBehaviors = arrayOf(
                ZeroPowerBehavior.BRAKE,
                ZeroPowerBehavior.FLOAT,
                ZeroPowerBehavior.UNKNOWN)
    }
}
