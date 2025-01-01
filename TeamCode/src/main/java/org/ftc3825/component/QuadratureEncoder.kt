package org.ftc3825.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.ftc3825.command.internal.GlobalHardwareMap
import java.util.function.DoubleSupplier

class QuadratureEncoder(
    private val hardwareDevice: DcMotor,
    override var direction: Component.Direction,
    override val ticksPerRevolution: Double = 0.0
): Encoder(){
    constructor(
        motorName: String,
        direction: Component.Direction,
        ticksPerRevolution: Double = 0.0
    ): this(
        GlobalHardwareMap.get(DcMotor::class.java, motorName),
        direction,
        ticksPerRevolution
    )

    override val supplier = DoubleSupplier {
        hardwareDevice.currentPosition.toDouble()
    }
}