package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.util.outtakeGripServoName
import org.firstinspires.ftc.teamcode.util.outtakePitchServoName
import org.firstinspires.ftc.teamcode.util.outtakeRollServoName
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.outtakePitch
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.wallPitch
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.rollUp
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.rollDown
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.grab
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.release

@Config object OuttakeClawConf{
    @JvmField var outtakePitch = 0.6
    @JvmField var wallPitch = 0.4

    @JvmField var rollUp = 0.66
    @JvmField var rollDown = 0.0

    @JvmField var grab = 0.0
    @JvmField var release = 1.0
}

object OuttakeClaw : Subsystem<OuttakeClaw> {

    val pitchServo = Servo(outtakePitchServoName, Servo.Range.GoBilda)
    val rollServo  = Servo(outtakeRollServoName, Servo.Range.GoBilda)
    val gripServo  = Servo(outtakeGripServoName, Servo.Range.GoBilda)

    override val components = arrayListOf<Component>(
        pitchServo,
        rollServo,
        gripServo
    )

    private var pinched = false

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
