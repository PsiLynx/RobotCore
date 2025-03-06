package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.GlobalHardwareMap
import java.util.function.DoubleSupplier

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