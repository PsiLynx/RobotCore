package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.Motor
import org.ftc3825.component.Servo
import org.ftc3825.util.LeftExtendoServoName
import org.ftc3825.util.RightExtendoServoName
import org.ftc3825.util.centimeters
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.slideMotorName
import org.ftc3825.command.internal.CommandScheduler

object Extendo: Subsystem<Extendo>() {
    val leftServo = Servo(LeftExtendoServoName)
    val rightServo = Servo(RightExtendoServoName)

    const val leftMax = 1.0
    const val leftMin = 0.0

    const val rightMax = 0.0
    const val rightMin = 1.0

    var target = 0.0

    override val motors
        get() = arrayListOf<Motor>()

    override fun update(deltaTime: Double) {
        setPosition(target)
        motors.forEach {
            it.update(deltaTime)
        }
    }

    fun setPosition(pos: Double) {
        leftServo.position  = leftMin  + pos * ( leftMax  - leftMin  )
        rightServo.position = rightMin + pos * ( rightMax - rightMin )
    }

    fun extend()  { setPosition(1.0) }
    fun retract() { setPosition(1.0) }
}
