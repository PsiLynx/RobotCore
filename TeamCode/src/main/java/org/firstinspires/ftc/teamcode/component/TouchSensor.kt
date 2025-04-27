package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.component.GlobalHardwareMap

class TouchSensor(name: String, val defualt: Boolean = false): Component {
    override var lastWrite = LastWrite.empty()
    override val hardwareDevice = GlobalHardwareMap.get(
        TouchSensor::class.java,
        name
    )

    val pressed: Boolean
        get() = hardwareDevice.isPressed xor defualt
    val status: String
        get() = if(pressed) "pressed" else "not pressed"

    override fun resetInternals() { }
    override fun update(deltaTime: Double) { }
}