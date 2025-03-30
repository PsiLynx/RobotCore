package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.AnalogSensor
import org.firstinspires.ftc.teamcode.command.internal.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import java.util.function.DoubleSupplier
import kotlin.math.PI

class AnalogEncoder(
    val name: String,
    val maxVoltage: Double,
): Encoder() {
    protected var rotations = 0
    val hardwareDevice = GlobalHardwareMap.get(AnalogSensor::class.java, name)

    override val posSupplier = DoubleSupplier {
        if(direction == FORWARD) hardwareDevice.readRawVoltage()
        else maxVoltage - hardwareDevice.readRawVoltage()
    }

    override var pos: Double
        get() = 2 * PI * ( currentPos / maxVoltage + offsetPos + rotations )
        set(value) {
            rotations = 0
            offsetPos = - currentPos * maxVoltage + value / ( 2 * PI )
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
        if(currentPos - lastPos >   maxVoltage / 2) rotations ++
        if(currentPos - lastPos < - maxVoltage / 2) rotations --
    }
}