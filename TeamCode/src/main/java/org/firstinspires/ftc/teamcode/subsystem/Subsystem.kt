package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap

interface Subsystem{
    fun init(hardwareMap: HardwareMap): Unit
}
