package org.ftc3825.subsystem

import org.ftc3825.component.Servo
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.util.intakeGripServoName
import org.ftc3825.util.intakeRollServoName
import org.ftc3825.util.intakePitchServoName

object SampleIntake : Subsystem<OuttakeClaw> {

    var pinched = false
    private val pitchServo = Servo(intakePitchServoName)
    private val rollServo = Servo(intakeRollServoName)
    private val gripServo = Servo(intakeGripServoName)

    override val components = arrayListOf<Component>(pitchServo, rollServo, gripServo)

    override fun update(deltaTime: Double) { }

    fun pitchForward() = InstantCommand { pitchServo.position = 1.0 }
    fun pitchDown() = InstantCommand { pitchServo.position = 0.5 }
    fun pitchBack() = InstantCommand { pitchServo.position = 0.0 }

    fun rollLeft() = InstantCommand { rollServo.position = 0.0 }
    fun rollCenter() = InstantCommand { rollServo.position = 0.5 }
    fun rollRight() = InstantCommand { rollServo.position = 1.0 }

    fun grab() = InstantCommand {
        gripServo.position = 0.7
        pinched = true
    }

    fun release() = InstantCommand {
        gripServo.position = 1.0
        pinched = false
    }

    fun toggleGrip() = InstantCommand {
        if(pinched) release().initialize()
        else        grab().initialize()
    }
}
