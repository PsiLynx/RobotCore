package org.firstinspires.ftc.teamcode.component

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareDevice

class LynxModule(
    val deviceSupplier: () -> LynxModule?
): Component() {
    private var _hwDeviceBacker: LynxModule? = null
    override val hardwareDevice: LynxModule get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }

    var ledColor = -1
        set(value) {
            hardwareDevice.setConstant(field)
            field = value

        }

    override fun resetInternals() {
        ledColor = -1
    }

    fun bulkRead() = hardwareDevice.clearBulkCache()

    override fun update(deltaTime: Double) { }

}