package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.teamcode.component.Component.Direction
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardware
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import kotlin.math.PI

class CRServo(
    hardwareDevice: HardwareDevice,
    name: String,
    ioOpTime: Double,
    direction: Direction,
    basePriority: Double,
    priorityScale: Double,
    range: Servo.Range,
): Motor(
    hardwareDevice,
    name,
    ioOpTime,
    direction,
    basePriority,
    priorityScale,
    (hardwareDevice as ServoImplEx).portNumber
) {

    init {
        addToDash(" CR Servos")

        hardwareDevice as ServoImplEx
        hardwareDevice.pwmRange =
            PwmRange(range.lower.toDouble(), range.upper.toDouble())
    }


    override fun doWrite(write: Optional<Double>) {
        hardwareDevice as ServoImplEx
        hardwareDevice.position = ( (write or 0.0) * direction.dir + 1 ) / 2
    }

}
