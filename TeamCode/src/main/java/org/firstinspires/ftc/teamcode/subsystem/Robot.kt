package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion


object Robot: Subsystem {
    override var initialized = false
    lateinit var voltageSensor: VoltageSensor

    override fun init(hardwareMap: HardwareMap) {
        if(!initialized) {
            voltageSensor = hardwareMap.get(VoltageSensor::class.java, "Control Hub")
        }
        initialized = true
    }

    val voltage: Double
        get() = voltageSensor.voltage
}