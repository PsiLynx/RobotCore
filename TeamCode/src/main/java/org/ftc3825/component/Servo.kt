package org.ftc3825.component

import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.command.internal.GlobalHardwareMap
import kotlin.math.abs

class Servo(name: String): Component {
    override val hardwareDevice: Servo = GlobalHardwareMap.get(Servo::class.java, name)

    override var lastWrite: Double? = null

    var position: Double
        get() = lastWrite?: 0.0
        set(pos) {
            if ( abs(pos - (lastWrite?: 100.0) ) <= EPSILON){
                return
            }

            hardwareDevice.position = pos
            lastWrite = pos
        }

    override fun resetInternals() { }
    override fun update(deltaTime: Double) { }

    companion object {
        const val EPSILON = 0.001 // goBilda torque servo deadband
    }
}
