package org.firstinspires.ftc.teamcode.sim

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.teamcode.fakehardware.JVMHardwareMap

object SimulatedHardwareMap: JVMHardwareMap() {
    override var deviceTypes: MutableMap<Class<out Any>, (String) -> HardwareDevice> =
        mutableMapOf(

        )



}