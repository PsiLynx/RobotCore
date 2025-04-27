package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.CRServo
import org.firstinspires.ftc.teamcode.component.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.util.control.PIDFController
import org.firstinspires.ftc.teamcode.util.control.PIDFGParameters
import kotlin.math.PI
import kotlin.math.abs

class CRServo(
    val name: String,
    var direction: Component.Direction,
    val ticksPerRev: Double = 1.0,
    val wheelRadius: Double = 1 / ( PI * 2 ),
): Actuator{
    override var lastWrite = LastWrite.empty()
    override val hardwareDevice: CRServo = GlobalHardwareMap.get(CRServo::class.java, name)
    override fun resetInternals() { }

    init { addToDash("CR Servos", name) }

    override fun set(value: Double?) {
        if(value == null) lastWrite = LastWrite.empty()
        else power = value
    }

    var encoder: Encoder? = null

    var ticks: Double
        get() = encoder?.pos ?: 0.0
        set(value) { encoder?.pos = value }
    val position: Double
        get() = ticks / ticksPerRev * wheelRadius * 2 * PI
    val velocity: Double
        get() = encoder?.delta ?: 0.0

    fun resetPosition(){ ticks = 0.0 }

    var power: Double = lastWrite or 0.0
        set(newPower) {
            if ( abs( newPower - (lastWrite or 100.0) ) < EPSILON ) { return }
            hardwareDevice.power = newPower * direction.dir
            println(newPower * direction.dir)
            lastWrite = LastWrite(newPower)
            field = lastWrite or 0.0
        }

    fun useEncoder(encoder: Encoder) { this.encoder = encoder }

    override fun update(deltaTime: Double) = encoder?.update(deltaTime) ?: Unit

    companion object { const val EPSILON = 0.005 }
}
