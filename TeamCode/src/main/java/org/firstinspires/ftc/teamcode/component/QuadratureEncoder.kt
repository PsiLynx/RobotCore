package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import java.util.function.DoubleSupplier

class QuadratureEncoder(
    private val hardwareDevice: DcMotor,
    override val uniqueName: String,
    override var direction: Component.Direction,
    override var ticksPerRev: Double,
    override var wheelRadius: Double,
): Encoder(){
    constructor(
        motorName: String,
        uniqueName: String,
        direction: Component.Direction,
        ticksPerRev: Double,
        wheelRadius: Double,
    ): this(
        HardwareMap.get(DcMotor::class.java, motorName),
        uniqueName,
        direction,
        ticksPerRev,
        wheelRadius,
    )

    override val posSupplier = DoubleSupplier {
        hardwareDevice.currentPosition.toDouble()
    }
}