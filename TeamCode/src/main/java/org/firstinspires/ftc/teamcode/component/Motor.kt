package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.config.Config
import kotlin.math.abs
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import org.firstinspires.ftc.teamcode.component.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.control.PIDFGParameters
import kotlin.math.PI
import org.firstinspires.ftc.teamcode.component.MotorConf.nominalVoltage
import org.firstinspires.ftc.teamcode.util.control.PIDFController

@Config object MotorConf {
    @JvmField var nominalVoltage = 13.0
}

class Motor (
    val name: String,
    rpm: Int,
    var direction: Direction = FORWARD,
    var wheelRadius: Double = 1.0,
    val controllerParameters: PIDFGParameters = PIDFGParameters()
): PIDFController(controllerParameters), Actuator {
    override val hardwareDevice: DcMotor = GlobalHardwareMap.get(DcMotor::class.java, name)
    override var lastWrite = LastWrite.empty()

    var encoder: Encoder? = null
    var ticksPerRev = 28 * 6000.0 / rpm

    var ticks = 0.0
    private var lastTicks = 0.0

    var velocity = 0.0
    private var lastVelocity = 0.0

    var acceleration = 0.0

    var setpoint = 0.0
    var feedbackComp = true
    private var useController = false
    var angle: Double
        get() = ( ( ticks / ticksPerRev  ) % 1 ) * 2 * PI
        set(value){
            encoder?.pos = ( value / ( 2 * PI ) % 1 ) * ticksPerRev
        }

    val position: Double
        get() = ticks / ticksPerRev * wheelRadius * 2 * PI
    override var pos = { position }

    var following: Motor? = null

    fun useInternalEncoder() {
        if(encoder == null){
            encoder = QuadratureEncoder(hardwareDevice, direction)
        }
    }
    init { addToDash("Motors", name) }

    override fun set(value: Double?) {
        if(value == null) lastWrite = LastWrite.empty()
        else power = value
    }

    override fun update(deltaTime: Double) {
        this.encoder?.update(deltaTime)

        lastTicks = ticks
        ticks = (encoder?.pos ?: 0.0)

        lastVelocity = velocity
        velocity = (ticks - lastTicks) / deltaTime
        acceleration = (velocity - lastVelocity) / deltaTime

        if(useController) {
            updateController(deltaTime)
        }

        if( following != null){
            power = following!!.lastWrite or 0.0
        }
    }

    fun resetPosition() = this.encoder?.resetPosition() ?: Unit

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
    var power: Double
        get() = lastWrite or 0.0
        set(value){
            if ( abs(value - (lastWrite or 100.0)) <= EPSILON ) return
            hardwareDevice.power = value * direction.dir

            lastWrite = LastWrite(value)
        }
    fun compPower(power: Double){
        this.power = power * ( nominalVoltage / Globals.robotVoltage )
    }

    override var apply = { feedback: Double ->
        if (feedbackComp) compPower(feedback)
        else power = feedback
    }
    override var setpointError = { setpoint - position }

    fun runToPosition(pos: Number, comp: Boolean){
        setpoint = pos.toDouble()
        useController = true
        feedbackComp = comp
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
