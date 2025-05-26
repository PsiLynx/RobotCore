package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Component

object Robot: Subsystem<Robot>() {
    private val voltageSensor: VoltageSensor = HardwareMap.get(VoltageSensor::class.java, "Control Hub")

    override val components: List<Component> = arrayListOf<Component>()

    val voltage: Double
        get() = voltageSensor.voltage

    override fun update(deltaTime: Double) { }
}
