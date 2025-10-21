package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Servo.Range
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.log

object Kicker: Subsystem<Kicker>(), Tunable<State.DoubleState> {
    override val tuningBack = DoubleState(0.0)
    override val tuningForward = DoubleState(1.0)
    override val tuningCommand = { it: State<*> ->
        runToPos((it as DoubleState).value) as Command
    }

    val servo = HardwareMap.kicker(range = Range.GoBilda)
    val sensor = HardwareMap.kickerSensor(default = true)

    val pressed get() = sensor.pressed

    override val components = listOf(servo)

    override fun update(deltaTime: Double) {
        log("pos") value servo.position
        log("pressed") value pressed
    }

    fun runToPos(pos: Double) = runOnce {
        servo.position = pos
    }
    fun runToPos(pos: () -> Double) = run {
        servo.position = pos()
    }

    fun open() = runToPos(0.84)
    fun close() = runToPos(0.0)
}
