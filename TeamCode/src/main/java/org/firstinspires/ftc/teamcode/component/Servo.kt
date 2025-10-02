package org.firstinspires.ftc.teamcode.component

import android.R.attr.value
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.teamcode.component.Optional.Companion.invoke
import org.firstinspires.ftc.teamcode.hardware.HardwareMap

class Servo(
    private val deviceSupplier: () -> ServoImplEx?,
    override val port: Int,
    ioOpTime: Double,
    basePriority: Double,
    priorityScale: Double,
    range: Range = Range.Default
): Actuator(ioOpTime, basePriority, priorityScale) {

    private var _hwDeviceBacker: ServoImplEx? = null
    override val hardwareDevice: ServoImplEx get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }

    var position: Double
        get() = lastWrite or 0.0
        set(value) {
            if( (lastWrite or 0.0) - value > 0.005 ) {
                lastWrite = Optional(value)
                doWrite(lastWrite)
            }

        }

    init {
        addToDash(" Servos")

        hardwareDevice.pwmRange =
            PwmRange(range.lower.toDouble(), range.upper.toDouble())
    }

    override fun doWrite(write: Optional<Double>) {
        hardwareDevice.position = write or 0.0
    }

    override fun set(value: Double?) {
        if(value == null) lastWrite = Optional.empty()
        else doWrite(Optional(value))
    }
    override fun resetInternals() { }
    override fun update(deltaTime: Double) { }

    enum class Range(val lower: Int, val upper: Int){
        Default(600, 2400), GoBilda(500, 2500);
    }

}
