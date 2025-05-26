package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.teamcode.hardware.HardwareMap

class Servo(
    name: String,
    basePriority: Double = 1.0,
    priorityScale: Double = 1.0,
    range: Range = Range.Default
): Actuator(basePriority, priorityScale) {
    override val ioOpTime = DeviceTimes.servo

    override val hardwareDevice: ServoImplEx =
        HardwareMap.get(Servo::class.java, name) as ServoImplEx

    var position: Double
        get() = lastWrite or 0.0
        set(pos) {
            targetWrite = Optional(pos)
        }

    init {
        addToDash("Servos", name)

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
