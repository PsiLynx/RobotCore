package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.component.CRServo.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.CRServo.Direction.REVERSE
import com.qualcomm.robotcore.hardware.HardwareMap

class CRServo(val name: String, val hardwareMap: HardwareMap) {
    var lastWrite = 0.0
    private var servo = hardwareMap.get(CRServo::class.java, name)

    var power: Double
        get() = lastWrite
        set(pos) {
            if (Math.abs(pos - lastWrite) <= epsilon) {
                return
            }
            servo.power = pos
            lastWrite = pos
        }
    var direction: Direction
        get() = if( servo.direction.equals(DcMotorSimple.Direction.FORWARD) ) FORWARD else REVERSE
        set(newDirection) {
            servo.direction =
                if (newDirection == FORWARD){ DcMotorSimple.Direction.FORWARD }
                else { DcMotorSimple.Direction.REVERSE }
        }

    enum class Direction(){
        FORWARD, REVERSE
    }

    companion object {
        const val epsilon = 0.005
    }
}
