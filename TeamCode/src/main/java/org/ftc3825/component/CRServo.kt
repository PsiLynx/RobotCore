package org.ftc3825.component

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.util.pid.PIDFControllerImpl
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.pid.PidController
import kotlin.math.abs

class CRServo(val name: String): Component, PIDFControllerImpl(){
    override var lastWrite = LastWrite.empty()
    override val hardwareDevice: CRServo = GlobalHardwareMap.get(CRServo::class.java, name)

    var setpoint = 0.0
        internal set
    var encoder: Encoder? = null
    var useFeedback = false
        internal set

    var position: Double
        get() = encoder!!.distance
        set(value) { encoder!!.distance = value }
    fun resetPosition(){ position = 0.0 }

    var power: Double
        get() = lastWrite or 0.0
        set(newPower) {
            val _pow = (
                    if(direction == REVERSE) -newPower
                       else newPower
                    )

            if ( abs( _pow - (lastWrite or 100.0) ) < EPSILON ) {
                return
            }
            hardwareDevice.power = _pow
            lastWrite = LastWrite(_pow)
        }

    var direction = FORWARD

    fun runToPosition(pos: Double) {
        useFeedback = true
        setpoint = pos
    }
    fun setPower(power: Double){ this.power = power}

    override fun getSetpointError(): Double{
        return setpoint - position
    }
    override fun applyFeedback(feedback: Double) { power = feedback }
    fun doNotFeedback(){ useFeedback = false }
    fun useEncoder(encoder: Encoder) { this.encoder = encoder }

    override fun resetInternals() { direction = FORWARD }
    override fun update(deltaTime: Double) {
        encoder?.update(deltaTime)
        if (useFeedback) {
            updateController(deltaTime)
        }
    }

    companion object { const val EPSILON = 0.005 }
}
