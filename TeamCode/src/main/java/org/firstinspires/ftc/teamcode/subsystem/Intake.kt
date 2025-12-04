package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Servo.Range
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.log

object Intake: Subsystem<Intake>() {

    val servo = HardwareMap.gate()
    val motor = HardwareMap.intake(FORWARD)

    override val components = listOf(motor, servo)

    val running get() = motor.power > 0.2

    override fun update(deltaTime: Double) {
        log("power") value motor.power
    }

    fun setPower(pow: Double) = run {
        motor.power = pow
    } withEnd { motor.power = 0.0 }

    fun run() = (
        setPower(1.0) parallelTo open()
        withEnd InstantCommand {
            close().command()
            motor.power = 0.0
        }
    )
    fun reverse() = (
        setPower(-1.0) parallelTo open()
        withEnd InstantCommand {
            close().command()
            motor.power = 0.0
        }
    )
    fun stop() = setPower(0.0) until { true } parallelTo close()

    fun close() = InstantCommand { servo.position = 0.0 }
    fun open()  = InstantCommand { servo.position = 0.2 }
}
