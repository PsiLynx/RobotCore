package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.AnalogInputController
import com.qualcomm.robotcore.util.SerialNumber

class FakeAnalogInputController(): AnalogInputController, FakeHardware {

    override fun getAnalogInputVoltage(channel: Int): Double {
        TODO("Not yet implemented")
    }
    override fun getMaxAnalogInputVoltage() = 3.3
    override fun getSerialNumber(): SerialNumber? { TODO("Not yet implemented") }
    override fun resetDeviceConfigurationForOpMode() { }

    override fun update(deltaTime: Double) { }

}
class FakeAnalogInput: AnalogInput(FakeAnalogInputController(), 0) {
    var _voltage = 0.0
    override fun getVoltage() = _voltage
    fun setVoltage(newVoltage: Double) { _voltage = newVoltage }
}
