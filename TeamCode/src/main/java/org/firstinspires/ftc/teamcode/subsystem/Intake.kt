package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import kotlinx.coroutines.withTimeout
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.Servo.Range
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.abs

@Config
object IntakeConf {
    @JvmField var backOutMotorPow = -0.5
    @JvmField var backOutTime = 0.5
}

object Intake: Subsystem<Intake>() {

    val intake1 = HardwareMap.intake1(FORWARD)
    val intake2 = HardwareMap.intake2(REVERSE)

    //block: 0.63 open: 0.73
    val blocker = HardwareMap.blocker()

    val threeBalls get() = (
        intake1.angularVelocity < 14
        && abs(intake1.acceleration) < 1
    )

    override val components = listOf(intake1, blocker)

    val running get() = intake1.power > 0.2

    init {
        intake1.encoder = HardwareMap.intakeEncoder(FORWARD, 28*3.0, 1.0)
    }

    override fun update(deltaTime: Double) {
        log("power") value intake1.power
        log("velocity") value intake1.angularVelocity
        log("accelaration") value intake1.acceleration
        log("three balls") value threeBalls

        log("blocker") value (
            if(blocker.position ==  0.1) "OPEN"
            else "CLOSED"
        )
    }

    fun backOut() = DeferredCommand {
        run(motorPow = IntakeConf.backOutMotorPow).withTimeout(
            IntakeConf.backOutTime
        )
    }

    fun setPower(pow: Double) = run {
        intake1.power = pow
        intake2.power = pow
    } withEnd {
        intake1.power = 0.0 
        intake2.power = 0.0 
    }

    fun run(
        blockerPos: Component.Opening = Component.Opening.CLOSED,
        motorPow: Double = 1.0
    ) = (
        run {
            intake1.power = motorPow
            intake2.power = motorPow

            blocker.position =
                if(blockerPos == Component.Opening.OPEN) 0.51
                else 0.71
        }
        withEnd {
            intake1.power = 0.0
            intake2.power = 0.0

        }
    ) withName "In: run"

    fun reverse() = (
        setPower(-1.0)
        withEnd InstantCommand {
            intake1.power = 0.0
            intake2.power = 0.0
        }
    ) withName "In: reverse"
    fun stop() = (
        setPower(0.0)
        until { true }
    ) withName "In: stop"

}
