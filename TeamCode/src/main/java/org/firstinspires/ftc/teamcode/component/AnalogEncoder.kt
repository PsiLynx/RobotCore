package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.AnalogSensor
import org.firstinspires.ftc.teamcode.command.internal.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import java.util.function.DoubleSupplier
import kotlin.math.PI

class AnalogEncoder(
    val name: String,
    val maxVoltage: Double,
    val zeroVoltage: Double
): Encoder() {
    val hardwareDevice = GlobalHardwareMap.get(AnalogInput::class.java, name)

    override val posSupplier = DoubleSupplier {
        ( (
            hardwareDevice.voltage
            + maxVoltage
            - zeroVoltage
        ) % maxVoltage ) / maxVoltage * 2 * PI
    }

    override var pos: Double
        get() = ( currentPos + offsetPos )
        set(value) {
            offsetPos = - currentPos + value
        }
    override val delta: Double
        get() = arrayListOf(
            currentPos - lastPos,
            currentPos - lastPos + 2 * PI,
            currentPos - lastPos - 2 * PI,
        ).min()
    val angle: Double
        get() = currentPos

    override fun update(deltaTime: Double){
        lastPos = currentPos
        currentPos = posSupplier.asDouble
    }
}