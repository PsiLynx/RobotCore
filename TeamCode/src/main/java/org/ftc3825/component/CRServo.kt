package org.ftc3825.component

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.reflection.FieldProvider
import com.qualcomm.robotcore.hardware.CRServo
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.util.pid.PIDFControllerImpl
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

class CRServo(
    val name: String,
    var direction: Component.Direction,
    val ticksPerRev: Double = 1.0,
    val wheelRadius: Double = 1 / ( PI * 2 )
): Component, PIDFControllerImpl(){
    override var lastWrite = LastWrite.empty()
    override val hardwareDevice: CRServo = GlobalHardwareMap.get(CRServo::class.java, name)
    override fun resetInternals() { }


    var setpoint = 0.0
        internal set
    var encoder: Encoder? = null
    var useFeedback = false
        internal set

    var ticks: Double
        get() = encoder?.distance ?: 0.0
        set(value) { encoder?.distance = value }
    val position: Double
        get() = ticks / ticksPerRev * wheelRadius * 2 * PI
    val velocity: Double
        get() = encoder?.delta ?: 0.0

    override var pos = { position }

    fun resetPosition(){ ticks = 0.0 }

    var power: Double = lastWrite or 0.0
        get() = field
        set(newPower) {
            if ( abs( newPower - (lastWrite or 100.0) ) < EPSILON ) { return }
            hardwareDevice.power = newPower * direction.dir
            println(newPower * direction.dir)
            lastWrite = LastWrite(newPower)
            field = lastWrite or 0.0
        }


    fun runToPosition(pos: Double) {
        useFeedback = true
        setpoint = pos
    }

    override var setpointError = { setpoint - position }
    override fun applyFeedback(feedback: Double) { power = feedback }
    fun doNotFeedback(){ useFeedback = false }
    fun useEncoder(encoder: Encoder) { this.encoder = encoder }

    override fun update(deltaTime: Double) {
        encoder?.update(deltaTime)
        if (useFeedback) {
            updateController(deltaTime)
        }
    }

    companion object { const val EPSILON = 0.005 }
}
