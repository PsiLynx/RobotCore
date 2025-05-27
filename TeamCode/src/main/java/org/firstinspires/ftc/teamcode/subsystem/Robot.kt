package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.IOComponent

object Robot: Subsystem<Robot>() {
    private val voltageSensor: VoltageSensor = HardwareMap.get(VoltageSensor::class.java, "Control Hub")

    override val components: List<IOComponent> = arrayListOf<IOComponent>()

    val voltage: Double
        get() = voltageSensor.voltage

    override fun update(deltaTime: Double) { }
}
