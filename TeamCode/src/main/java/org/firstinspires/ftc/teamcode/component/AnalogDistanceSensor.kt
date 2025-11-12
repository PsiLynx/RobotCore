package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.AnalogInput

class AnalogDistanceSensor(
    val deviceSupplier: () -> AnalogInput?,
    val minDist: Double = 0.0,
    val maxDist: Double = 1.0,
    val zeroVoltage: Double = 0.0,
    val maxVoltage: Double = 3.3,
) {
    private var _hwDeviceBacker: AnalogInput? = null
    val hardwareDevice: AnalogInput get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }

    val distance get() = (
        (hardwareDevice.voltage - zeroVoltage)
        / ( maxVoltage - zeroVoltage)
        * ( maxDist - minDist )
        + minDist
    )
}