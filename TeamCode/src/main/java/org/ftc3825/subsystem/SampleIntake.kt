package org.ftc3825.subsystem

import com.acmerobotics.dashboard.config.Config

import org.ftc3825.component.Servo
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.subsystem.IntakeConf.pitchDown
import org.ftc3825.subsystem.IntakeConf.pitchBack
import org.ftc3825.subsystem.IntakeConf.pitchTransfer
import org.ftc3825.subsystem.IntakeConf.beforeClipPitch
import org.ftc3825.subsystem.IntakeConf.clippedPitch
import org.ftc3825.subsystem.IntakeConf.rollBack
import org.ftc3825.subsystem.IntakeConf.rollLeft
import org.ftc3825.subsystem.IntakeConf.rollCenter
import org.ftc3825.subsystem.IntakeConf.rollRight
import org.ftc3825.subsystem.IntakeConf.grab
import org.ftc3825.subsystem.IntakeConf.release
import org.ftc3825.subsystem.IntakeConf.looselyHold
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.degrees
import org.ftc3825.util.intakeGripServoName
import org.ftc3825.util.intakeRollServoName
import org.ftc3825.util.intakePitchServoName

@Config
object IntakeConf {
    @JvmField var pitchDown = 0.05
    @JvmField var pitchBack = 0.5
    @JvmField var pitchTransfer = 0.5
    @JvmField var beforeClipPitch = 0.4 //TODO: tune
    @JvmField var clippedPitch = 0.3 //TODO: tune

    @JvmField var rollBack = 1.0
    @JvmField var rollLeft = 0.71
    @JvmField var rollCenter = 0.38
    @JvmField var rollRight = 0.05

    @JvmField var grab = 0.4
    @JvmField var release = 0.95
    @JvmField var looselyHold = 0.75 //TODO: tune
}

object SampleIntake : Subsystem<SampleIntake> {

    val pitchServo = Servo(intakePitchServoName, Servo.Range.goBilda)
    val rollServo = Servo(intakeRollServoName, Servo.Range.goBilda)
    val gripServo = Servo(intakeGripServoName, Servo.Range.goBilda)

    override val components = arrayListOf<Component>(
        pitchServo,
        rollServo,
        gripServo
    )
    val minRoll = degrees(0) //TODO: get accurate degrees
    val maxRoll = degrees(300)
    var roll = 0.0

    private var pinched = false

    override fun update(deltaTime: Double) { }

    fun pitchDown() = InstantCommand { pitchServo.position = pitchDown }
    fun pitchBack() = InstantCommand { pitchServo.position = pitchBack }
    fun pitchTransfer() = InstantCommand { pitchServo.position = pitchTransfer }
    fun beforeClipPitch() = InstantCommand { pitchServo.position = beforeClipPitch }
    fun clippedPitch() = InstantCommand { pitchServo.position = clippedPitch }

    fun rollCenter() = InstantCommand {
        roll = rollCenter
        rollServo.position = rollCenter
    }
    fun rollBack() = InstantCommand {
        roll = rollBack
        rollServo.position = rollBack
    }
    fun rollLeft() = InstantCommand {
        roll = rollLeft
        rollServo.position = rollLeft
    }

    fun nudgeLeft() = InstantCommand {
        roll = ( roll + 0.1 ).coerceIn(0.0, 1.0)
        println(roll)
        rollServo.position = roll
    }
    fun nudgeRight() = InstantCommand {
        roll = ( roll - 0.1 ).coerceIn(0.0, 1.0)
        println(roll)
        rollServo.position = roll
    }
    fun setAngle(angle: Rotation2D) {
        rollServo.position = (angle.toDouble() - minRoll) /
            (maxRoll - minRoll)
    }
    fun autoIntakeAngle() = InstantCommand {
        roll = 0.2
    }

    fun grab() = InstantCommand {
        gripServo.position = grab
        pinched = true
    }

    fun release() = InstantCommand {
        gripServo.position = release
        pinched = false
    }

    fun looselyHold() = InstantCommand { gripServo.position = looselyHold }

    fun toggleGrip() = InstantCommand {
        if (pinched) {
            gripServo.position = release
        } else {
            gripServo.position = grab
        }
        pinched = !pinched
    }

    override fun reset() = components.forEach { it.reset() }
}

