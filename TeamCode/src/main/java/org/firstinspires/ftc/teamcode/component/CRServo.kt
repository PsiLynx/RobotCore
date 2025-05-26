package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import kotlin.math.PI

class CRServo(
    name: String,
    direction: Direction,
    basePriority: Double = 1.0,
    priorityScale: Double = 1.0,
    range: Servo.Range = Servo.Range.Default
): Motor(
    name,
    direction,
    basePriority,
    priorityScale
) {
    override val ioOpTime = DeviceTimes.crServo
    override val hardwareDevice: ServoImplEx =
        HardwareMap.get(
            com.qualcomm.robotcore.hardware.Servo::class.java,
            name
        ) as ServoImplEx

    init {
        addToDash("Servos", name)

        hardwareDevice.pwmRange =
            PwmRange(range.lower.toDouble(), range.upper.toDouble())
    }

    override fun doWrite(write: Optional<Double>) {
        hardwareDevice.position = ( (write or 0.0) * direction.dir + 1 ) / 2
    }

}
