package org.ftc3825.component

import kotlin.math.abs
import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Component.Direction
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.util.millimeters
import org.ftc3825.util.pid.PIDFControllerImpl
import org.ftc3825.util.pid.PIDFGParameters
import kotlin.math.PI

class Motor (
    val name: String,
    rpm: Int,
    var direction: Direction = FORWARD,
    var wheelRadius: Double = millimeters(24),
    val controllerParameters: PIDFGParameters = PIDFGParameters()
): PIDFControllerImpl(), Component {

    override val hardwareDevice: DcMotor = GlobalHardwareMap.get(DcMotor::class.java, name)
    override var lastWrite = LastWrite.empty()
    var encoder: Encoder? = null
    val ticksPerRev = 28 * 6000.0 / rpm 

    var ticks = 0.0
    private var lastTicks = 0.0

    var velocity = 0.0
    private var lastVelocity = 0.0

    var acceleration = 0.0

    private var setpoint = 0.0
    private var useController = false
    val angle: Double
        get() = ( ticks / ticksPerRev ) % ( 2 * PI )

    val position: Double
        get() = ticks / ticksPerRev * wheelRadius * 2 * PI

    var following: Motor? = null

    init { initializeController(controllerParameters) }

    fun useInternalEncoder() {
        if(encoder == null){
            encoder = QuadratureEncoder(hardwareDevice, direction, ticksPerRev)
        }
    }

    override fun update(deltaTime: Double) {
        this.encoder?.update(deltaTime)

        lastTicks = ticks
        ticks = (encoder?.distance ?: 0.0)

        lastVelocity = velocity
        velocity = (ticks - lastTicks) / deltaTime
        acceleration = (velocity - lastVelocity) / deltaTime

        if(useController) {
            updateController(deltaTime)
        }

        if( following != null){
            setPower((following!!.lastWrite or 0.0))
        }
    }

    fun resetPosition() = this.encoder?.resetPosition()

    fun setZeroPowerBehavior(behavior: ZeroPower) {
        hardwareDevice.zeroPowerBehavior = zeroPowerBehaviors[behavior]
    }

    fun follow(other: Motor) {
        following = other
    }

    override fun resetInternals() {
        setpoint = 0.0
        useController = false

        ticks = 0.0
        lastTicks = 0.0
        velocity = 0.0
        lastVelocity = 0.0
        acceleration = 0.0

        resetController()
    }
    fun setPower(speed: Double) {
        val _pow = if(direction == REVERSE) -speed
                   else speed

        if ( abs(_pow - (lastWrite or 100.0)) <= EPSILON ){
            return
        }
        hardwareDevice.power = _pow
        lastWrite = LastWrite(_pow)
    }

    override fun applyFeedback(feedback: Double) { setPower(feedback) }
    override fun getSetpointError() =  setpoint - ticks

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

    companion object {
        const val EPSILON = 0.005 //less than this and you don't write to the motor
        val zeroPowerBehaviors = mapOf(
                ZeroPower.BRAKE to ZeroPowerBehavior.BRAKE,
                ZeroPower.FLOAT to ZeroPowerBehavior.FLOAT,
                ZeroPower.UNKNOWN to ZeroPowerBehavior.UNKNOWN)
    }
}
