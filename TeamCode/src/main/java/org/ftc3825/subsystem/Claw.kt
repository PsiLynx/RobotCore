package org.ftc3825.subsystem

import org.ftc3825.component.Motor
import org.ftc3825.component.Servo
import org.ftc3825.component.CRServo
import org.ftc3825.stateMachine.State
import org.ftc3825.stateMachine.StateMachine
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.degrees
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.util.pitchServoName
import org.ftc3825.util.rollServoName
import org.ftc3825.util.gripServoName

object Claw : Subsystem<Claw>() {
    override val components = arrayListOf<Component>()

    val pitchServo = Servo(pitchServoName)
    val rollServo = Servo(rollServoName)
    val gripServo = Servo(gripServoName)

    var pinched = false

    override fun update(deltaTime: Double) { }

    fun pitchUp() { pitchServo.position = 1.0 }
    fun pitchDown() { pitchServo.position = 0.0 }

    fun rollLeft() { rollServo.position = 0.2 }
    fun rollCenter() { rollServo.position = 0.48 }
    fun rollRight() { rollServo.position = 0.8 }

    fun grab() {
        gripServo.position = 0.7
        pinched = true
        Unit
    }

    fun release() {
        gripServo.position = 1.0
        pinched = false
        Unit
    }

    fun toggleGrip() {
        if(pinched) {
            gripServo.position = 1.0
            pinched = false
        }
        else{
            gripServo.position = 0.7
            pinched = true
        }
    }
}
