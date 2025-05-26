package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.CRServo
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import kotlin.math.PI

class CRServo(
    name: String,
    direction: Direction,
    basePriority: Double = 1.0,
    priorityScale: Double = 1.0,
): Motor(
    name,
    direction,
    basePriority,
    priorityScale
) {
    override val ioOpTime = DeviceTimes.crServo
    override val hardwareDevice: CRServo = HardwareMap.get(CRServo::class.java, name)

    init { addToDash("CR Servos", name) }

    override fun doWrite(write: Optional<Double>) {
        hardwareDevice.power = (write or 0.0) * direction.dir
    }

}
