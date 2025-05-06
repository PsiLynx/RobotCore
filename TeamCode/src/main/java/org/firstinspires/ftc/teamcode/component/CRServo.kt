package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.CRServo
import kotlin.math.PI
import kotlin.math.abs

class CRServo(
    name: String,
    direction: Direction,
    override var ticksPerRev: Double = 1.0,
    wheelRadius: Double = 1 / ( PI * 2 ),
    basePriority: Double = 1.0,
    priorityScale: Double = 1.0,
): Motor(
    name,
    1,
    direction,
    wheelRadius,
    basePriority,
    priorityScale
) {
    override val ioOpTime = DeviceTimes.crServo
    override val hardwareDevice: CRServo = GlobalHardwareMap.get(CRServo::class.java, name)

    init { addToDash("CR Servos", name) }

    override fun doWrite(write: Write) {
        hardwareDevice.power = (write or 0.0) * direction.dir
    }

}
