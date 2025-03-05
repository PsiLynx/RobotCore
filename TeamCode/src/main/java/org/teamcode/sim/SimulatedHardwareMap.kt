package org.teamcode.sim

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.teamcode.fakehardware.JVMHardwareMap

object SimulatedHardwareMap: JVMHardwareMap() {
    override var deviceTypes: MutableMap<Class<out Any>, (String) -> HardwareDevice> =
        mutableMapOf(
            DcMotor::class.java to { SimulatedMotor() }
        )



}