package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.teamcode.component.GlobalHardwareMap
import kotlin.math.abs

class Servo(name: String, range: Range = Range.Default): Actuator {
    override val hardwareDevice: ServoImplEx =
        GlobalHardwareMap.get(Servo::class.java, name) as ServoImplEx

    override var lastWrite = LastWrite.empty()

    var position: Double = 0.0
        set(pos) {
            if ( abs(pos - (lastWrite or 100.0) ) <= EPSILON){ return }

            hardwareDevice.position = pos
            lastWrite = LastWrite(pos)
            field = lastWrite or 0.0
        }

    init {
        addToDash("Servos", name)

        hardwareDevice.pwmRange =
            PwmRange(range.lower.toDouble(), range.upper.toDouble())
    }

    override fun set(value: Double?) {
        if(value == null) lastWrite = LastWrite.empty()
        else position = value
    }
    override fun resetInternals() { }
    override fun update(deltaTime: Double) { }

    enum class Range(val lower: Int, val upper: Int){
        Default(600, 2400), GoBilda(500, 2500);
    }

    companion object {
        const val EPSILON = 0.001 // goBilda torque servo deadband
    }
}
