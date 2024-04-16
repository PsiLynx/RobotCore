package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class Servo(val name: String, val hardwareMap: HardwareMap, val min: Double = 0.0, val max: Double = 0.0) {
    var lastWrite = 0.0
    var servo: com.qualcomm.robotcore.hardware.Servo

    init {
        servo = hardwareMap.get(Servo::class.java, name)
    }

    fun setAngle(angle: Double) {
        if (angle >= min && angle <= max) {
            val pos = (angle - min) / max //lerp from min to max
            position = pos
        }
    }

    var position: Double
        get() = lastWrite
        set(pos) {
            if (abs(pos - lastWrite) <= epsilon) {
                return
            }
            servo.position = pos
            lastWrite = pos
        }

    companion object {
        const val epsilon = 0.005
    }
}
