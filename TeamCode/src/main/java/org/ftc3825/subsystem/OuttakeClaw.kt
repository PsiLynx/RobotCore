package org.ftc3825.subsystem

import com.acmerobotics.dashboard.config.Config
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Servo
import org.ftc3825.util.outtakeGripServoName
import org.ftc3825.util.outtakePitchServoName
import org.ftc3825.util.outtakeRollServoName
import org.ftc3825.subsystem.OuttakeClawConf.intakePitch
import org.ftc3825.subsystem.OuttakeClawConf.outtakePitch
import org.ftc3825.subsystem.OuttakeClawConf.wallPitch
import org.ftc3825.subsystem.OuttakeClawConf.rollUp
import org.ftc3825.subsystem.OuttakeClawConf.rollDown
import org.ftc3825.subsystem.OuttakeClawConf.grab
import org.ftc3825.subsystem.OuttakeClawConf.release


@Config object OuttakeClawConf{
    @JvmField var intakePitch = 0.1
    @JvmField var outtakePitch = 0.5
    @JvmField var wallPitch = 0.35

    @JvmField var rollUp = 0.65
    @JvmField var rollDown = 0.05

    @JvmField var grab = 0.0
    @JvmField var release = 1.0
}

object OuttakeClaw : Subsystem<OuttakeClaw> {

    val pitchServo = Servo(outtakePitchServoName, Servo.Range.goBilda)
    val rollServo  = Servo(outtakeRollServoName, Servo.Range.goBilda)
    val gripServo  = Servo(outtakeGripServoName, Servo.Range.goBilda)

    override val components = arrayListOf<Component>(
        pitchServo,
        rollServo,
        gripServo
    )

    private var pinched = false

    fun intakePitch() = InstantCommand { pitchServo.position = intakePitch }
    fun outtakePitch() = InstantCommand { pitchServo.position = outtakePitch }
    fun wallPitch() = InstantCommand { pitchServo.position = wallPitch }

    fun rollUp() = InstantCommand { rollServo.position = rollUp; println("roll up") }
    fun rollDown() = InstantCommand { rollServo.position = rollDown; println("roll down") }

    fun grab() = InstantCommand {
        gripServo.position = grab
        pinched = true
    }

    fun release() = InstantCommand {
        gripServo.position = release
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
