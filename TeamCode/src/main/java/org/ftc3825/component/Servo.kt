package org.ftc3825.component

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.reflection.FieldProvider
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.ftc3825.command.internal.GlobalHardwareMap
import kotlin.math.abs

class Servo(name: String, range: Range = Range.defualt): Component {
    override val hardwareDevice: ServoImplEx =
        GlobalHardwareMap.get(Servo::class.java, name) as ServoImplEx

    init {
        hardwareDevice.pwmRange = PwmRange(range.lower.toDouble(), range.upper.toDouble())
    }

    override var lastWrite = LastWrite.empty()

    var position: Double = lastWrite or 0.0
        set(pos) {
            if ( abs(pos - (lastWrite or 100.0) ) <= EPSILON){ return }

            hardwareDevice.position = pos
            lastWrite = LastWrite(pos)
            field = lastWrite or 0.0
        }

    override fun resetInternals() { }
    override fun update(deltaTime: Double) { }

    enum class Range(val lower: Int, val upper: Int){
        defualt(600, 2400), goBilda(500, 2500);
    }

    companion object {
        const val EPSILON = 0.001 // goBilda torque servo deadband
    }
}
