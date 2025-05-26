package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand

import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.subsystem.IntakeConf.pitchDown
import org.firstinspires.ftc.teamcode.subsystem.IntakeConf.pitchBack
import org.firstinspires.ftc.teamcode.subsystem.IntakeConf.rollBack
import org.firstinspires.ftc.teamcode.subsystem.IntakeConf.rollLeft
import org.firstinspires.ftc.teamcode.subsystem.IntakeConf.rollCenter
import org.firstinspires.ftc.teamcode.subsystem.IntakeConf.grab
import org.firstinspires.ftc.teamcode.subsystem.IntakeConf.release
import org.firstinspires.ftc.teamcode.subsystem.IntakeConf.looselyHold
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.intakeGripServoName
import org.firstinspires.ftc.teamcode.util.intakeRollServoName
import org.firstinspires.ftc.teamcode.util.intakePitchServoName
import kotlin.math.PI

@Config
object IntakeConf {
    @JvmField var pitchDown = 0.57
    @JvmField var pitchBack = 0.9
    @JvmField var pitchTransfer = 0.5
    @JvmField var beforeClipPitch = 0.4 //TODO: tune
    @JvmField var clippedPitch = 0.3 //TODO: tune

    @JvmField var rollBack = 1.0
    @JvmField var rollLeft = 0.71
    @JvmField var rollCenter = 0.38
    @JvmField var rollRight = 0.05

    @JvmField var grab = 0.0
    @JvmField var release = 0.95
    @JvmField var looselyHold = 0.75 //TODO: tune
}

object SampleIntake : Subsystem<SampleIntake>() {

    val pitchServo = HWQue.servo(
        intakePitchServoName, 1.0, 1.0, Servo.Range.GoBilda
    )
    val rollServo = HWQue.servo(
        intakeRollServoName, 1.0, 1.0, Servo.Range.GoBilda
    )
    val gripServo = HWQue.servo(
        intakeGripServoName, 1.0, 1.0, Servo.Range.GoBilda
    )

    override val components: List<Component> = arrayListOf<Component>(
        pitchServo,
        rollServo,
        gripServo
    )
    val minRoll = degrees(-20) //TODO: get accurate degrees
    val maxRoll = degrees(280)
    var roll = 0.0

    val SM = CyclicalCommand(
        pitchBack() parallelTo rollLeft() withName "back",
        pitchDown() parallelTo rollCenter() withName "down",
    )

    private var pinched = false

    override fun update(deltaTime: Double) { }

    fun pitchDown() = InstantCommand { pitchServo.position = pitchDown }
    fun pitchBack() = InstantCommand { pitchServo.position = pitchBack }

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
        roll = ( roll + 0.15 ).coerceIn(0.0, 1.0)
        println(roll)
        rollServo.position = roll
    }
    fun nudgeRight() = InstantCommand {
        roll = ( roll - 0.15 ).coerceIn(0.0, 1.0)
        println(roll)
        rollServo.position = roll
    }
    fun setAngle(angle: Rotation2D) {
	val unwraped = if(angle < - PI / 2) 2 * PI + angle.toDouble() else angle.toDouble()
        roll = ( (unwraped - minRoll) / (maxRoll - minRoll) ).coerceIn(0.0, 1.0)
        rollServo.position = roll
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

