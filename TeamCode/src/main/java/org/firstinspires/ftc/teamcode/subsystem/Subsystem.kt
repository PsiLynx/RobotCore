package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap

interface Subsystem{
    fun init(hardwareMap: HardwareMap): Unit
    fun update(deltaTime: Double = 0.0)

    var initialized: Boolean
}
