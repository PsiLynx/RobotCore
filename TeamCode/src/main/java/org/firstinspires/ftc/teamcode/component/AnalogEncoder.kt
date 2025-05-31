package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.AnalogInput
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import java.util.function.DoubleSupplier
import kotlin.math.PI

class AnalogEncoder(
    private val port: Int,
    val maxVoltage: Double,
    val zeroVoltage: Double,
    override val wheelRadius: Double = 1.0
): Encoder() {
    override val ticksPerRev = 1.0

    override val posSupplier = DoubleSupplier {
        ( (
            HWManager.BulkData.analog[port]
            + maxVoltage
            - zeroVoltage
        ) % maxVoltage ) / maxVoltage
    }

    override val delta: Double
        get() = arrayListOf(
            currentPos - lastPos,
            currentPos - lastPos + 2 * PI,
            currentPos - lastPos - 2 * PI,
        ).min()

    override fun update(deltaTime: Double){
        lastPos = currentPos
        currentPos = posSupplier.asDouble
    }
}