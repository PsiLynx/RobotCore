package org.ftc3825.subsystem

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Servo
import org.ftc3825.util.clipFlipServoName
import org.ftc3825.util.clipGripServoName
import org.ftc3825.util.clipPitchServoName

object ClipIntake : Subsystem<OuttakeClaw> {

    var clips = BooleanArray(8) { _ -> false}
    val pitchServo = Servo(clipPitchServoName)
    val flipServo = Servo(clipFlipServoName)
    val gripServo = Servo(clipGripServoName)

    override val components = arrayListOf<Component>(pitchServo, flipServo, gripServo)

    override fun update(deltaTime: Double) { }

    fun pitchLeft() = InstantCommand { pitchServo.position = 1.0 }
    fun pitchRight() = InstantCommand { pitchServo.position = 0.0 }
    fun clippedPitch() = InstantCommand { pitchServo.position = 0.1 } //TODO: tune
    fun beforeClippedPitch() = InstantCommand { pitchServo.position = 0.2 } //TODO: tune

    fun flipBack() = InstantCommand { flipServo.position = 0.0 }
    fun flipForward() = InstantCommand { flipServo.position = 0.6 }

    fun above() = InstantCommand { gripServo.position = 1.0 } //TODO: tune
    fun grab() = InstantCommand { gripServo.position = 0.5 }
    fun release() = InstantCommand { gripServo.position = 0.0 }

}
