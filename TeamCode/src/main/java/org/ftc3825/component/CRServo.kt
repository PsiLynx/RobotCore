package org.ftc3825.component

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.CRServo.Direction.FORWARD
import org.ftc3825.component.CRServo.Direction.REVERSE
import org.ftc3825.util.isWithin
import org.ftc3825.util.of

class CRServo(val name: String): Component {
    override var lastWrite = LastWrite.empty()
    override val hardwareDevice: CRServo = GlobalHardwareMap.get(CRServo::class.java, name)

    override fun resetInternals() {
        direction = FORWARD
    }

    override fun update(deltaTime: Double) { }

    var power: Double
        get() = lastWrite or 0.0
        set(newPower) {
            var _pow = newPower
            if(direction == REVERSE) {
                _pow = -newPower
            }
            if ( _pow isWithin EPSILON of (lastWrite or 100.0) ) {
                return
            }

            hardwareDevice.power = _pow
            lastWrite = LastWrite(_pow)
        }

    var direction: Direction
        get() = if( hardwareDevice.direction.equals(DcMotorSimple.Direction.FORWARD) ) FORWARD else REVERSE
        set(newDirection) {
            hardwareDevice.direction =
                if (newDirection == FORWARD){ DcMotorSimple.Direction.FORWARD }
                else { DcMotorSimple.Direction.REVERSE }
        }

    enum class Direction {
        FORWARD, REVERSE
    }

    companion object {
        const val EPSILON = 0.005
    }
}
