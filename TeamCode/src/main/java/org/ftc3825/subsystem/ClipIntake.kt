package org.ftc3825.subsystem

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Servo
import org.ftc3825.util.clipFlipServoName
import org.ftc3825.util.clipGripServoName
import org.ftc3825.util.clipPitchServoName

object ClipIntake : Subsystem<OuttakeClaw> {

    var pinched = false
    private val pitchServo = Servo(clipPitchServoName)
    private val flipServo = Servo(clipFlipServoName)
    private val gripServo = Servo(clipGripServoName)

    override val components = arrayListOf<Component>(pitchServo, flipServo, gripServo)

    override fun update(deltaTime: Double) { }

    fun pitchLeft() = InstantCommand { pitchServo.position = 1.0 }
    fun pitchRight() = InstantCommand { pitchServo.position = 0.0 }

    fun flipBack() = InstantCommand { flipServo.position = 0.0 }
    fun flipForward() = InstantCommand { flipServo.position = 0.6 }

    fun grab() = InstantCommand {
        gripServo.position = 1.0
        pinched = true
    }

    fun release() = InstantCommand {
        gripServo.position = 0.0
        pinched = false
    }

    fun toggleGrip() = (
        if(pinched) release()
        else        grab()
    )
}
