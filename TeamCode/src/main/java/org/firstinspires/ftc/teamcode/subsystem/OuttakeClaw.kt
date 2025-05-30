package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.ramPitch
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.outtakePitch
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.wallPitch
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.rollUp
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.rollDown
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.grab
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf.release

@Config object OuttakeClawConf{
    @JvmField var ramPitch = 0.6
    @JvmField var outtakePitch = 0.54
    @JvmField var wallPitch = 0.4

    @JvmField var rollUp = 0.0
    @JvmField var rollDown = 0.6

    @JvmField var grab = 0.0
    @JvmField var release = 1.0

    @JvmField var intakeWait = 0.3
}

object OuttakeClaw : Subsystem<OuttakeClaw>() {

    val pitchServo = HardwareMap.outtakePitch(1.0, 1.0, Servo.Range.GoBilda)
    val rollServo  = HardwareMap.outtakeRoll( 1.0, 1.0, Servo.Range.GoBilda)
    val gripServo  = HardwareMap.outtakeRoll( 1.0, 1.0, Servo.Range.GoBilda)

    override val components: List<Component> = arrayListOf<Component>(
        pitchServo,
        rollServo,
        gripServo
    )

    private var pinched = false

    fun ramPitch() = InstantCommand { pitchServo.position = ramPitch }
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
