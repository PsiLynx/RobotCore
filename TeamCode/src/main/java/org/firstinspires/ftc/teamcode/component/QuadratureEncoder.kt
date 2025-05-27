package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import java.util.function.DoubleSupplier

class QuadratureEncoder(
    private val hardwareDevice: DcMotor,
    override var direction: Component.Direction,
    override var ticksPerRev: Double,
    override var wheelRadius: Double,

    ): Encoder(){
    constructor(
        motorName: String,
        direction: Component.Direction,
        ticksPerRev: Double,
        wheelRadius: Double,
    ): this(
        HardwareMap.get(DcMotor::class.java, motorName),
        direction,
        ticksPerRev,
        wheelRadius
    )

    override val posSupplier = DoubleSupplier {
        hardwareDevice.currentPosition.toDouble()
    }
}