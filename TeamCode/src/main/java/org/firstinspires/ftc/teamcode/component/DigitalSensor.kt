package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DigitalChannel

class DigitalSensor<T>(
    private val deviceSupplier: () -> DigitalChannel?,
    private val falseValue: T,
    private val trueValue: T
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

    val value get() = (
        if(hardwareDevice.state == true) trueValue
        else falseValue
    )

}