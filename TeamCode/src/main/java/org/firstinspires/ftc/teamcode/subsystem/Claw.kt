package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Servo

class Claw(hardwareMap: HardwareMap) : Subsystem(hardwareMap) {
    var left: Servo
    var right: Servo

    init {
        left = Servo("left", hardwareMap)
        right = Servo("right", hardwareMap)
    }

    fun openLeft() {
        left.position = open
    }

    fun openRight() {
        right.position = open
    }

    fun closeLeft() {
        left.position = closed
    }

    fun closeRight() {
        right.position = closed
    }

    companion object{
        const val open = 1.0
        const val closed = 0.0
    }
}
