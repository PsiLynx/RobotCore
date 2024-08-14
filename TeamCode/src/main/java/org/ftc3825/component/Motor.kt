package org.ftc3825.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.util.isWithin
import org.ftc3825.util.millimeters
import org.ftc3825.util.of
import org.ftc3825.util.pid.PIDFControllerImpl
import org.ftc3825.util.pid.PIDFGParameters
import kotlin.math.PI

class Motor (
    val name: String,
    val hardwareMap: HardwareMap,
    rpm: Int,
    var wheelRadius: Double = millimeters(24),
    val direction: Direction = Direction.FORWARD,
    val controllerParameters: PIDFGParameters = PIDFGParameters()
): PIDFControllerImpl() {


    val motor: DcMotor
    var lastWrite: Double = 0.0
    var encoder: Encoder? = null
    val ticksPerRev = 28 * 6000.0 / rpm //Nevrest motors have 6,000 rpm base and 28 ticks per revolution

    var position = 0.0
    private var lastPos = 0.0

    var velocity = 0.0
    private var lastVelocity = 0.0

    var acceleration = 0.0

    private var setpoint = 0.0
    private var useController = false

    init {
        initializeController(controllerParameters)

        motor = hardwareMap.get(DcMotor::class.java, name)
        motor.direction = (
            if(direction == Direction.FORWARD) DcMotorSimple.Direction.FORWARD
            else DcMotorSimple.Direction.REVERSE
        )
    }


    fun useInternalEncoder() {
        encoder = Encoder(motor, ticksPerRev, wheelRadius=wheelRadius)
    }

    fun update(deltaTime: Double) {
        this.encoder?.update()

        lastPos = position
        position = (encoder?.distance ?: 0.0) / (wheelRadius * 2 * PI) * ticksPerRev

        lastVelocity = velocity
        velocity = (position - lastPos) / deltaTime
        acceleration = (velocity - lastVelocity) / deltaTime

        if(useController) {
            updateController(deltaTime)
        }
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
    fun reset() {
        setpoint = 0.0
        useController = false

        position = 0.0
        lastPos = 0.0
        velocity = 0.0
        lastVelocity = 0.0
        acceleration = 0.0

        resetController()

        motor.resetDeviceConfigurationForOpMode()

    }
    fun setPower(speed: Double) {
        val _speed = speed.coerceIn(-1.0, 1.0)
        if ( !( _speed isWithin EPSILON of lastWrite) ) {
            lastWrite = _speed
            motor.power = _speed
        }
    }

    override fun applyFeedback(feedback: Double) { setPower(feedback) }
    override fun getSetpointError() =  setpoint - position

    fun runToPosition(pos: Number){ setpoint = pos.toDouble(); useController = true }
    fun doNotFeedback() {
        useController = false
    }

    enum class ZeroPower {
        FLOAT, BRAKE, UNKNOWN
    }
    enum class Direction {
        FORWARD, REVERSE
    }

    companion object {
        const val EPSILON = 0.005 //less than this and you don't write to the motors
        val zeroPowerBehaviors = mapOf(
                ZeroPower.FLOAT to ZeroPowerBehavior.BRAKE,
                ZeroPower.BRAKE to ZeroPowerBehavior.FLOAT,
                ZeroPower.UNKNOWN to ZeroPowerBehavior.UNKNOWN)
    }
}
