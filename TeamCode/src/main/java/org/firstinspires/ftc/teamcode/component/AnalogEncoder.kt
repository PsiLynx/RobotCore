package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.AnalogInput
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import java.util.function.DoubleSupplier
import kotlin.math.PI

class AnalogEncoder(
    val name: String,
    val maxVoltage: Double,
    val zeroVoltage: Double,
    override val wheelRadius: Double = 1.0
): Encoder() {
    val hardwareDevice = HardwareMap.get(AnalogInput::class.java, name)

    override val ticksPerRev = 1.0

    override val posSupplier = DoubleSupplier {
        ( (
            hardwareDevice.voltage
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