package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.VoltageSensor
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component

object Robot: Subsystem<Robot> {
    private val voltageSensor: VoltageSensor = GlobalHardwareMap.get(VoltageSensor::class.java, "Control Hub")

    override val components = arrayListOf<Component>()

    val voltage: Double
        get() = voltageSensor.voltage

    override fun update(deltaTime: Double) { }
}
