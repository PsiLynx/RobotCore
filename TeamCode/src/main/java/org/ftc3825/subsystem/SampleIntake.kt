package org.ftc3825.subsystem

import org.ftc3825.component.Servo
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.degrees
import org.ftc3825.util.intakeGripServoName
import org.ftc3825.util.intakeRollServoName
import org.ftc3825.util.intakePitchServoName

object SampleIntake : Subsystem<OuttakeClaw> {

    var pinched = false
    val pitchServo = Servo(intakePitchServoName)
    val rollServo = Servo(intakeRollServoName)
    val gripServo = Servo(intakeGripServoName)
    val minRoll = degrees(0) //TODO: get accurate degrees
    val maxRoll = degrees(300)

    override val components = arrayListOf<Component>(
        pitchServo,
        rollServo,
        gripServo
    )

    override fun update(deltaTime: Double) { }

    fun pitchForward() = InstantCommand { pitchServo.position = 1.0 }
    fun pitchDown() = InstantCommand { pitchServo.position = 0.5 }
    fun pitchBack() = InstantCommand { pitchServo.position = 0.0 }
    fun beforeClipPitch() = InstantCommand { pitchServo.position = 0.4 } //TODO: tune
    fun clippedPitch() = InstantCommand { pitchServo.position = 0.3 } //TODO: tune

    fun rollLeft() = InstantCommand { rollServo.position = 0.0 }
    fun rollCenter() = InstantCommand { rollServo.position = 0.5 }
    fun rollRight() = InstantCommand { rollServo.position = 1.0 }
    fun setAngle(angle: Rotation2D) = InstantCommand {
        rollServo.position = (angle.toDouble() - minRoll) / (maxRoll - minRoll)
    }

    fun grab() = InstantCommand { gripServo.position = 0.7 } //TODO: tune
    fun release() = InstantCommand { gripServo.position = 1.0 }
    fun looslyHold() = InstantCommand { gripServo.position = 0.8 } //TODO: tune

}
