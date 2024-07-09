package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.inches
import kotlin.math.abs

class Motor(
    val name: String,
    val hardwareMap: HardwareMap,
    val rpm: Int,
    var gearRatio: Double = 1.0,
    var Kstatic: Double = 0.0,
    var wheelRadius: Double = inches(1.0),
    val direction: Direction = Direction.FORWARD
) {

    val motor: DcMotor
    var lastWrite: Double = 0.0
    var ticksPerRev: Double = 1.0
    var encoder: Encoder? = null

    init {
        ticksPerRev = 28 * 6000.0 / rpm //Nevrest motors have 6,000 rpm base and 28 ticks per revolution
        motor = hardwareMap.get(DcMotor::class.java, name)
        motor.direction = (
            if(direction == Direction.FORWARD) DcMotorSimple.Direction.FORWARD
            else DcMotorSimple.Direction.REVERSE
        )
    }


    fun useInternalEncoder() {
        encoder = Encoder(motor, ticksPerRev, wheelRadius=wheelRadius)
    }

    var position: Double = 0.0
        private set(value){
            field = value
        }
    var lastPos = 0.0
        private set(value) {
            field = value
        }

    var velocity = 0.0
        private set(value){
            field = value
        }
    var lastVelocity = 0.0
        private set(value) {
            field = value
        }

    val acceleration: Double
        get() = velocity - lastVelocity
    fun update() {
        lastPos = position
        position = encoder?.distance ?: 0.0

        lastVelocity = velocity
        velocity = position - lastPos
    }

    /**
     * angle of the motor in degrees.
     * actually a wrapper for encoder.angle.
     * if encoder == null, return 0.0
     */
    val angle: Double
        get():Double{
            return encoder?.angle ?: 0.0
        }

    fun setZeroPowerBehavior(behavior: ZeroPower) {
        motor.zeroPowerBehavior = zeroPowerBehaviors[behavior]
    }

    fun setDirection(direction: Direction) {
        when (direction) {
            Direction.FORWARD -> {
                motor.direction = DcMotorSimple.Direction.FORWARD
            }

            Direction.REVERSE -> {
                 motor.direction = DcMotorSimple.Direction.REVERSE
            }
        }
    }

    fun setPower(speed: Double) {
        var speed = speed
        if (abs(speed - lastWrite) < EPSILON) {
            return
        }
        speed = (1 - Kstatic) * speed + Kstatic //lerp from Kstatic to 1
        lastWrite = speed
        motor.power = speed
    }
    enum class ZeroPower(){
        FLOAT, BRAKE, UNKNOWN
    }
    enum class Direction(){
        FORWARD, REVERSE
    }

    companion object {
        const val EPSILON = 0.005 //less than this and you don't write to the motors
        val zeroPowerBehaviors = mapOf(
                ZeroPower.FLOAT to  ZeroPowerBehavior.BRAKE,
                ZeroPower.BRAKE to  ZeroPowerBehavior.FLOAT,
                ZeroPower.UNKNOWN to  ZeroPowerBehavior.UNKNOWN)
    }
}
