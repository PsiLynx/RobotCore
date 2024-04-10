package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class Servo {
    var name: String
    var hardwareMap: HardwareMap
    var min = 0.0 //angle
    var max = 0.0 //angle
    var lastWrite = 0.0
    var servo: com.qualcomm.robotcore.hardware.Servo? = null

    constructor(name: String, hardwareMap: HardwareMap) {
        this.name = name
        this.hardwareMap = hardwareMap
        servo = hardwareMap.get(Servo::class.java, name)
    }

    constructor(name: String, hardwareMap: HardwareMap, min: Double, max: Double) {
        this.name = name
        this.hardwareMap = hardwareMap
        this.servo = hardwareMap.get(Servo::class.java, name)
        this.min = min
        this.max = max
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
            if (Math.abs(pos - lastWrite) <= epsilon) {
                return
            }
            servo!!.position = position
            lastWrite = pos
        }

    companion object {
        const val epsilon = 0.005
    }
}
