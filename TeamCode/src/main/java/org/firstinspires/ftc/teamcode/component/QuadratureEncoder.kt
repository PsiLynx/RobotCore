package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import java.util.function.DoubleSupplier

class QuadratureEncoder(
    private val port: Int,
    override var direction: Component.Direction,
    override var ticksPerRev: Double,
    override var wheelRadius: Double,
): Encoder(){
    override val posSupplier = DoubleSupplier {
        HWManager.BulkData.quadrature[port]
    }
}