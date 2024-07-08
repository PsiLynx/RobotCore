package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.VoltageSensor

class FakeVoltageSensor: FakeHardware(), VoltageSensor {
    var _voltage = 12.0
    override fun getVoltage() = _voltage
    fun setVoltage(v: Number){
        _voltage = v.toDouble()
    }

}