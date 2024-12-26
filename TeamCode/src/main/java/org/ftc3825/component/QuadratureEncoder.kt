package org.ftc3825.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.GlobalHardwareMap
import java.util.function.DoubleSupplier

class QuadratureEncoder(
    motorName: String,
    override var direction: Component.Direction,
    override val ticksPerRevolution: Double = 0.0
): Encoder(){
    private val hardwareDevice = GlobalHardwareMap.get(
        DcMotor::class.java,
        motorName
    )

    override val supplier = DoubleSupplier {
        hardwareDevice.currentPosition.toDouble()
    }
}