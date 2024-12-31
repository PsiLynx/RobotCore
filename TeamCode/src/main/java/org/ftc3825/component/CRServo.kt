package org.ftc3825.component

import com.qualcomm.robotcore.hardware.CRServo
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.util.pid.PIDFControllerImpl
import kotlin.math.abs

class CRServo(val name: String): Component, PIDFControllerImpl(){
    override var lastWrite = LastWrite.empty()
    override val hardwareDevice: CRServo = GlobalHardwareMap.get(CRServo::class.java, name)

    var setpoint = 0.0
        internal set
    var encoder: Encoder? = null
    var useFeedback = false
        internal set

    var ticks: Double
        get() = encoder?.distance ?: 0.0
        set(value) { encoder?.distance = value }
    val position: Double
        get() = ticks * ( encoder?.ticksPerRevolution ?: 0.0 )
    val velocity: Double
        get() = encoder?.delta ?: 0.0

    fun resetPosition(){ ticks = 0.0 }

    var power: Double
        get() = lastWrite or 0.0
        set(newPower) {
            val _pow = if(direction == REVERSE) -newPower
                       else newPower

            if ( abs( _pow - (lastWrite or 100.0) ) < EPSILON ) { return }
            hardwareDevice.power = _pow
            lastWrite = LastWrite(_pow)
        }

    var direction = FORWARD

    fun runToPosition(pos: Double) {
        useFeedback = true
        setpoint = pos
    }

    override fun getSetpointError(): Double{
        return setpoint - ticks
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
