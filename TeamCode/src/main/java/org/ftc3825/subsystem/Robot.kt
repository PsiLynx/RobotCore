package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.ftc3825.component.Motor
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap

object Robot: Subsystem<Robot>() {
    val voltageSensor = GlobalHardwareMap.get(VoltageSensor::class.java, "Control Hub")

    override val motors = arrayListOf<Motor>()

    val voltage: Double
        get() = voltageSensor.voltage

    override fun update(deltaTime: Double) { }
}
