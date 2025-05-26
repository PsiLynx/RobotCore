package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap

class TouchSensor(name: String, val default: Boolean = false) {
    val hardwareDevice = HardwareMap.get(
        TouchSensor::class.java,
        name
    )

    val pressed: Boolean
        get() = hardwareDevice.isPressed xor default
    val status: String
        get() = if(pressed) "pressed" else "not pressed"
}