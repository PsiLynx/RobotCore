package org.teamcode.subsystem

import com.qualcomm.robotcore.hardware.VoltageSensor
import org.teamcode.command.internal.GlobalHardwareMap
import org.teamcode.component.Component

object Robot: Subsystem<Robot> {
    private val voltageSensor: VoltageSensor = GlobalHardwareMap.get(VoltageSensor::class.java, "Control Hub")

    override val components = arrayListOf<Component>()

    val voltage: Double
        get() = voltageSensor.voltage

    override fun update(deltaTime: Double) { }
}
