package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.DigitalChannelController

class FakeDigitalChannel: DigitalChannel, FakeHardware {
    private var _mode = DigitalChannel.Mode.INPUT
    private var _state = false
    override fun getMode() = _mode

    override fun setMode(mode: DigitalChannel.Mode) {
        _mode = mode
    }

    override fun getState() = _state

    override fun setState(state: Boolean) {
        _state = state
    }

    override fun setMode(mode: DigitalChannelController.Mode?) {
        _mode = if(mode == DigitalChannelController.Mode.INPUT) {
            DigitalChannel.Mode.INPUT
        } else DigitalChannel.Mode.OUTPUT
    }

    override fun resetDeviceConfigurationForOpMode() { }

    override fun update(deltaTime: Double) { }
}