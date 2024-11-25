package org.ftc3825.subsystem

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Servo
import org.ftc3825.util.gripServoName
import org.ftc3825.util.pitchServoName
import org.ftc3825.util.rollServoName

object Claw : Subsystem<Claw> {
    override val components = arrayListOf<Component>()

    private val pitchServo = Servo(pitchServoName)
    private val rollServo = Servo(rollServoName)
    private val gripServo = Servo(gripServoName)

    var pinched = false

    override fun update(deltaTime: Double) { }

    fun pitchUp() = runOnce { pitchServo.position = 1.0 }
    fun pitchDown() = runOnce { pitchServo.position = 0.0 }
    fun groundSpecimenPitch() = runOnce { pitchServo.position = 0.3 }

    fun rollLeft() = runOnce { rollServo.position = 0.2 }
    fun rollCenter() = runOnce { rollServo.position = 0.48 }
    fun rollRight() = runOnce { rollServo.position = 0.8 }

    fun grab() = runOnce {
        gripServo.position = 0.7
        pinched = true
    }

    fun release() = runOnce {
        gripServo.position = 1.0
        pinched = false
    }

    fun toggleGrip() = runOnce {
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
