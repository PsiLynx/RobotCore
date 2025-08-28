package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap

class TouchSensor(
    val hardwareDevice: DigitalChannel,
    val default: Boolean = false) {
    val pressed: Boolean
        get() = ( hardwareDevice.state ) xor default

    val status: String
        get() = if(pressed) "pressed" else "not pressed"
}