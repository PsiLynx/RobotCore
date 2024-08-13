package org.ftc3825.sim

import com.qualcomm.robotcore.hardware.HardwareDevice
import org.ftc3825.fakehardware.JVMHardwareMap

object SimulatedHardwareMap: JVMHardwareMap() {
    override var deviceTypes: MutableMap<Class<out Any>, (String) -> HardwareDevice> =
        mutableMapOf(

        )



}