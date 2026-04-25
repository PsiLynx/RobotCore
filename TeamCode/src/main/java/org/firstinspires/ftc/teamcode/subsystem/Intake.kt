package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.command.internal.Command
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

object Intake: Subsystem<Intake>() {

    val intake1 = HardwareMap.intake(FORWARD)

    //block: 0.63 open: 0.73
    val blocker = HardwareMap.blocker()

    // connected: 0.9 away: 0.4
    val propeller = HardwareMap.propeller()

    val threeBalls get() = (
        intake1.angularVelocity < 14
        && abs(intake1.acceleration) < 1
    )

    override val components = listOf(intake1, blocker, propeller)

    val running get() = intake1.power > 0.2

    init {
        intake1.encoder = HardwareMap.intakeEncoder(FORWARD, 28*3.0, 1.0)
    }

    override fun update(deltaTime: Double) {
        log("power") value intake1.power
        log("velocity") value intake1.angularVelocity
        log("accelaration") value intake1.acceleration
        log("three balls") value threeBalls

        log("propeller") value (
            if(propeller.position == 0.5) "OPEN"
            else "CLOSED"
        )

        log("blocker") value (
            if(blocker.position ==  0.73) "OPEN"
            else "CLOSED"
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
        propellerPos: Component.Opening = Component.Opening.OPEN,
        blockerPos: Component.Opening = Component.Opening.CLOSED,
        motorPow: Double = 1.0
    ) = (
        run {
            intake1.power = motorPow
            intake2.power = motorPow
            propeller.position =
                if(propellerPos == Component.Opening.OPEN) 0.5
                else 0.9

            blocker.position =
                if(blockerPos == Component.Opening.OPEN) 0.73
                else 0.6
        }
        withEnd {
            intake1.power = 0.2
            intake2.power = 0.2

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
