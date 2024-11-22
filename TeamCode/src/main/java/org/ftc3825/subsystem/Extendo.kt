package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.Motor
import org.ftc3825.component.Servo
/*
import org.ftc3825.util.LeftExtendoServoName
import org.ftc3825.util.RightExtendoServoName
*/
import org.ftc3825.util.centimeters
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.slideMotorName
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Component

object Extendo: Subsystem<Extendo> {
    override val components
        get() = arrayListOf<Component>()

    override fun update(deltaTime: Double) {
        /*
        setPosition(target)
        components.forEach {
            it.update(deltaTime)
        }
        */
    }
    /*
    val leftServo = Servo(LeftExtendoServoName)
    val rightServo = Servo(RightExtendoServoName)

    const val leftMax = 1.0
    const val leftMin = 0.0

    const val rightMax = 0.0
    const val rightMin = 1.0

    var target = 0.0


    fun setPosition(pos: Double) {
        leftServo.position  = leftMin  + pos * ( leftMax  - leftMin  )
        rightServo.position = rightMin + pos * ( rightMax - rightMin )
    }

    fun extend()  { setPosition(1.0) }
    fun retract() { setPosition(1.0) }
    */
}
