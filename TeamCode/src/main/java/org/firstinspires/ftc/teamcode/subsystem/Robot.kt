package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor


object Robot: Subsystem {
    override var initialized = false
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