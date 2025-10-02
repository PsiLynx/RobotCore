package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.ServoImplEx

class TouchSensor(
    private val deviceSupplier: () -> DigitalChannel?,
    val default: Boolean = false
) {

    private var _hwDeviceBacker: DigitalChannel? = null
    val hardwareDevice: DigitalChannel get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }
    val pressed: Boolean
        get() = ( hardwareDevice.state ) xor default

    val status: String
        get() = if(pressed) "pressed" else "not pressed"
}