package org.ftc3825.component

import kotlin.math.abs
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.util.isWithin
import org.ftc3825.util.millimeters
import org.ftc3825.util.of
import org.ftc3825.util.pid.PIDFControllerImpl
import org.ftc3825.util.pid.PIDFGParameters
import kotlin.math.PI

class Motor (
    val name: String,
    rpm: Int,
    var direction: Direction = Direction.FORWARD,
    var wheelRadius: Double = millimeters(24),
    val controllerParameters: PIDFGParameters = PIDFGParameters()
): PIDFControllerImpl() {


    val motor = GlobalHardwareMap.get(DcMotor::class.java, name)
    var lastWrite: Double? = 0.0
    var encoder: Encoder? = null
    val ticksPerRev = 28 * 6000.0 / rpm 

    var position = 0.0
    private var lastPos = 0.0

    var velocity = 0.0
    private var lastVelocity = 0.0

    var acceleration = 0.0

    private var setpoint = 0.0
    private var useController = false

    var following: Motor? = null

    init { initializeController(controllerParameters) }

    fun useInternalEncoder() {
        if(encoder == null){
            encoder = Encoder(motor, ticksPerRev)
        }
    }

    fun update(deltaTime: Double) {
        this.encoder?.update()

        lastPos = position
        position = (encoder?.distance ?: 0.0)

        lastVelocity = velocity
        velocity = (position - lastPos) / deltaTime
        acceleration = (velocity - lastVelocity) / deltaTime

        if(useController) {
            updateController(deltaTime)
        }

        if( following != null){
            //updateError(deltaTime)
            setPower((following!!.lastWrite?:0.0))
        }
    }

    fun setZeroPowerBehavior(behavior: ZeroPower) {
        motor.zeroPowerBehavior = zeroPowerBehaviors[behavior]
    }

    fun follow(other: Motor) {
        following = other
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
        var _pow = speed
        if(direction == Direction.REVERSE) {
            _pow = -speed
        }
        //if ( abs(_pow - (lastWrite ?: 100.0)) <= EPSILON ){
            //return
        //}

        motor.power = _pow
        lastWrite = _pow
    }

    override fun applyFeedback(feedback: Double) { setPower(feedback) }
    override fun getSetpointError() =  setpoint - position

    fun runToPosition(pos: Number){
        setpoint = pos.toDouble()
        useController = true 
    }

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
        const val EPSILON = 0.000 //less than this and you don't write to the motors
        val zeroPowerBehaviors = mapOf(
                ZeroPower.BRAKE to ZeroPowerBehavior.BRAKE,
                ZeroPower.FLOAT to ZeroPowerBehavior.FLOAT,
                ZeroPower.UNKNOWN to ZeroPowerBehavior.UNKNOWN)
    }
}
