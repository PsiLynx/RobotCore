package org.ftc3825.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.ftc3825.command.internal.GlobalHardwareMap
import java.util.function.DoubleSupplier
import kotlin.math.PI

class QuadratureEncoder(
    private val hardwareDevice: DcMotor,
    override var direction: Component.Direction,
): Encoder(){
    constructor(
        motorName: String,
        direction: Component.Direction,
    ): this(
        GlobalHardwareMap.get(DcMotor::class.java, motorName),
        direction,
    )

    override val supplier = DoubleSupplier {
        hardwareDevice.currentPosition.toDouble()
    }
}