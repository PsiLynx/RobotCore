package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.ftc3825.component.Motor


object Robot: Subsystem<Robot> {
    override var initialized = false
    override val motors = arrayListOf<Motor>()
    lateinit var voltageSensor: VoltageSensor

    val voltage: Double
        get() = voltageSensor.voltage

    override fun init(hardwareMap: HardwareMap) {
        if(!initialized) {
            voltageSensor = hardwareMap.get(VoltageSensor::class.java, "Control Hub")
        }
        initialized = true
    }

    override fun update(deltaTime: Double) { }
}