package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.logging.Input

class TouchSensor(
    name: String,
    override val uniqueName: String,
    val default: Boolean = false
):
    Input<org.firstinspires.ftc.teamcode.component.TouchSensor> {
    val hardwareDevice = HardwareMap.get(
        TouchSensor::class.java,
        name
    )

    val pressed: Boolean get() = ( getValue()[0] == 1.0 ) xor default
    val status: String
        get() = if(pressed) "pressed" else "not pressed"


    override fun getRealValue() = arrayOf(
        if(hardwareDevice.isPressed) 1.0 else 0.0
    )
}