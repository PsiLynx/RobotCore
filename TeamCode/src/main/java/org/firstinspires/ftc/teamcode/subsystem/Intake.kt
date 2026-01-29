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

object Intake: Subsystem<Intake>() {

    val motor = HardwareMap.intake(FORWARD)

    //block: 0.63 open: 0.73
    val blocker = HardwareMap.blocker()

    // connected: 0.9 away: 0.4
    val propeller = HardwareMap.propeller()

    val transferLeft  = HardwareMap.transferLeft(FORWARD)
    val transferRight = HardwareMap.transferRight(REVERSE)

    override val components = listOf(motor, blocker, propeller)

    val running get() = motor.power > 0.2

    init {
        motor.encoder = HardwareMap.intakeEncoder(FORWARD, 28*3.0, 1.0)
    }

    override fun update(deltaTime: Double) {
        log("power") value motor.power
        log("velocity") value motor.angularVelocity
    }

    fun setPower(pow: Double) = run {
        motor.power = pow
    } withEnd { motor.power = 0.0 }

    fun run(
        propellerPos: Component.Opening = Component.Opening.OPEN,
        blockerPos: Component.Opening = Component.Opening.CLOSED,
        transferSpeed: Double = 0.0,
        motorPow: Double = 1.0
    ) = (
        run {
            motor.power = motorPow
            transferLeft.power = transferSpeed
            transferRight.power = transferSpeed
            propeller.position =
                if(propellerPos == Component.Opening.OPEN) 0.5
                else 0.9

            blocker.position =
                if(blockerPos == Component.Opening.OPEN) 0.73
                else 0.6
        }
        withEnd {
            motor.power = 0.2
            transferLeft.power = 0.0
            transferRight.power = 0.0
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
