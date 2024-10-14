package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.ftc3825.component.Motor
import org.ftc3825.command.internal.CommandScheduler

object Robot: Subsystem<Robot>() {
    override val motors = arrayListOf<Motor>()
    lateinit var voltageSensor: VoltageSensor

    init{
        init(CommandScheduler.hardwareMap)
    }

    val voltage: Double
        get() = voltageSensor.voltage

    override fun init(hardwareMap: HardwareMap) {
        voltageSensor = hardwareMap.get(VoltageSensor::class.java, "Control Hub")
    }

    override fun update(deltaTime: Double) { }
}
