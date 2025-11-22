package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import java.util.function.DoubleSupplier
import kotlin.math.PI

class AnalogEncoder(
    val deviceSupplier: () -> AnalogInput?,
    val maxVoltage: Double,
    val zeroVoltage: Double,
    override val wheelRadius: Double = 1.0
): Encoder() {

    private var _hwDeviceBacker: AnalogInput? = null
    val hardwareDevice: AnalogInput get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }
    override val ticksPerRev = 1.0

    override val posSupplier = DoubleSupplier {
        ( (
            hardwareDevice.voltage
            + maxVoltage
            - zeroVoltage
        ) % maxVoltage ) / maxVoltage
    }


    override fun update(deltaTime: Double){
        lastPos = currentPos
        currentPos = posSupplier.asDouble
    }
}