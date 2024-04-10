package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.math.abs

class Motor(private val name: String, private val hardwareMap: HardwareMap, private val rpm: Int) {

    private var gearRatio = 0.0
    private var Kstatic = 0.0
    private val lastWrite = 0.0
    private val motor: DcMotor
    private var encoder: Encoder? = null
    private var ticksPerRev = 1.0
    private var wheelRadius = 1.0
    private val direction = 1

    init {
        ticksPerRev = 28 * 6000.0 / rpm //Nevrest motors have 6,000 rpm base and 28 ticks per revolution
        motor = hardwareMap.get(DcMotor::class.java, name)
    }

    fun setKstatic(Kstatic: Double) {
        this.Kstatic = Kstatic
    }

    fun setGearRatio(gearRatio: Double) {
        this.gearRatio = gearRatio
    }

    fun useInternalEncoder() {
        encoder = Encoder(motor, ticksPerRev, wheelRadius)
    }

    fun setWheelRadius(radius: Double) {
        wheelRadius = radius
    }

    fun setTicksPerRev(ticksPerRev: Double) {
        this.ticksPerRev = ticksPerRev
    }

    val positsion: Int
        get() = motor.currentPosition

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
        if (abs(speed - lastWrite) < epsilon) {
            return
        }
        speed = (1 - Kstatic) * speed + Kstatic //lerp from Kstatic to 1
        motor.power = speed
    }

    companion object {
        const val epsilon = 0.005 //less than this and you don't write to the motors
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
