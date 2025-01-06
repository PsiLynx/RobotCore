package org.ftc3825.subsystem

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Servo
import org.ftc3825.util.outtakeGripServoName
import org.ftc3825.util.outtakePitchServoName
import org.ftc3825.util.outtakeRollServoName

object OuttakeClaw : Subsystem<OuttakeClaw> {

    private val pitchServo = Servo(outtakePitchServoName)
    private val rollServo  = Servo(outtakeRollServoName)
    private val gripServo  = Servo(outtakeGripServoName)

    override val components = arrayListOf<Component>(
        pitchServo,
        rollServo,
        gripServo
    )

    private var pinched = false

    val pitch
        get() = pitchServo.position


    fun pitchUp() = InstantCommand { pitchServo.position = 1.0}
    fun pitchDown() = InstantCommand { pitchServo.position = 0.0}
    fun outtakePitch() = InstantCommand { pitchServo.position = 0.5} //TODO: make correct

    fun rollLeft() = InstantCommand { rollServo.position = 0.2 }
    fun rollCenter() = InstantCommand { rollServo.position = 0.48 }
    fun rollRight() = InstantCommand { rollServo.position = 0.8 }

    fun grab() = InstantCommand {
        gripServo.position = 0.7
        pinched = true
    }

    fun release() = InstantCommand {
        gripServo.position = 1.0
        pinched = false
    }

    fun toggleGrip() = InstantCommand {
        if(pinched) {
            gripServo.position = 1.0
            pinched = false
        }
        else{
            gripServo.position = 0.7
            pinched = true
        }
    }

    override fun update(deltaTime: Double) { }

    override fun reset() = components.forEach { it.reset() }
}
