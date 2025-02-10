package org.ftc3825.subsystem

import com.acmerobotics.dashboard.config.Config
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Servo
import org.ftc3825.subsystem.ClipIntake.flipServo
import org.ftc3825.subsystem.ClipIntake.gripServo
import org.ftc3825.subsystem.ClipIntake.pitchServo
import org.ftc3825.util.clipFlipServoName
import org.ftc3825.util.clipGripServoName
import org.ftc3825.util.clipPitchServoName
import org.ftc3825.subsystem.ClipConf.pitchLeft
import org.ftc3825.subsystem.ClipConf.pitchRight
import org.ftc3825.subsystem.ClipConf.clippedPitch
import org.ftc3825.subsystem.ClipConf.beforeClippedPitch
import org.ftc3825.subsystem.ClipConf.flipBack
import org.ftc3825.subsystem.ClipConf.flipForward
import org.ftc3825.subsystem.ClipConf.above
import org.ftc3825.subsystem.ClipConf.grab
import org.ftc3825.subsystem.ClipConf.release
@Config object ClipConf {
    @JvmField var pitchLeft          = 1.0
    @JvmField var pitchRight         = 0.0
    @JvmField var clippedPitch       = 0.1
    @JvmField var beforeClippedPitch = 0.2

    @JvmField var flipBack    = 0.0
    @JvmField var flipForward = 0.6

    @JvmField var above   = 1.0
    @JvmField var grab    = 0.0
    @JvmField var release = 0.8
}

object ClipIntake : Subsystem<OuttakeClaw> {

    var clips = BooleanArray(8) { _ -> false}
    val pitchServo = Servo(clipPitchServoName, Servo.Range.goBilda)
    val flipServo = Servo(clipFlipServoName, Servo.Range.goBilda)
    val gripServo = Servo(clipGripServoName, Servo.Range.goBilda)

    override val components = arrayListOf<Component>(pitchServo, flipServo, gripServo)

    override fun update(deltaTime: Double) { }

    fun pitchLeft() = InstantCommand { pitchServo.position = pitchLeft }
    fun pitchRight() = InstantCommand { pitchServo.position = pitchRight }
    fun clippedPitch() = InstantCommand { pitchServo.position = clippedPitch}
    fun beforeClippedPitch() = InstantCommand { pitchServo.position = beforeClippedPitch }

    fun flipBack() = InstantCommand { flipServo.position = flipBack }
    fun flipForward() = InstantCommand { flipServo.position = flipForward }

    fun above() = InstantCommand { gripServo.position = above }
    fun grab() = InstantCommand { gripServo.position = grab }
    fun release() = InstantCommand { gripServo.position = release }

}
