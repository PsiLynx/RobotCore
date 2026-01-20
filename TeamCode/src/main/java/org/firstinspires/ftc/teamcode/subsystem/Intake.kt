package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
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

object Intake: Subsystem<Intake>() {

    val motor = HardwareMap.intake(FORWARD)

    //block: 0.63 open: 0.73
    val blocker = HardwareMap.blocker()

    // connected: 0.9 away: 0.4
    val propeller = HardwareMap.propeller()

    override val components = listOf(motor, blocker, propeller)

    val running get() = motor.power > 0.2

    override fun update(deltaTime: Double) {
        log("power") value motor.power
    }

    fun setPower(pow: Double) = run {
        motor.power = pow
    } withEnd { motor.power = 0.0 }

    fun run(
        propellerPos: Component.Opening = Component.Opening.OPEN,
        blockerPos: Component.Opening = Component.Opening.CLOSED,
        motorPow: Double = 1.0
    ) = (
        run {
            motor.power = motorPow
            propeller.position =
                if(propellerPos == Component.Opening.OPEN) 0.5
                else 0.9

            blocker.position =
                if(blockerPos == Component.Opening.OPEN) 0.73
                else 0.63
        }
        withEnd {
            motor.power = 0.0
        }
    ) withName "In: run"
    fun reverse() = (
        setPower(-1.0)
        withEnd InstantCommand {
            motor.power = 0.0
        }
    ) withName "In: reverse"
    fun stop() = (
        setPower(0.0)
        until { true }
    ) withName "In: stop"

}
