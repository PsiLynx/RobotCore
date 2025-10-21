package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Servo.Range
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.log

object Intake: Subsystem<Intake>(), Tunable<State.DoubleState> {
    override val tuningBack = DoubleState(0.0)
    override val tuningForward = DoubleState(1.0)
    override val tuningCommand = { it: State<*> ->
        setPower((it as DoubleState).value) as Command
    }

    val motor = HardwareMap.intake(FORWARD, 1.0, 1.0)

    override val components = listOf(motor)

    override fun update(deltaTime: Double) {
        log("power") value motor.power
    }

    fun setPower(pow: Double) = run {
        motor.power = pow
    } withEnd { motor.power = 0.0 }

    fun run() = setPower(1.0)
    fun stop() = setPower(0.0) until { true }
}
