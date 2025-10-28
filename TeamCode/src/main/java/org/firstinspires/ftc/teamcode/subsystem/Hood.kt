package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Servo.Range
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.min

object Hood: Subsystem<Hood>(), Tunable<DoubleState> {
    override val tuningBack = DoubleState(0.0)
    override val tuningForward = DoubleState(1.0)
    override val tuningCommand = { it: State<*> ->
        setAngle((it as DoubleState).value) as Command
    }

    val minAngle = degrees(11)
    val maxAngle = degrees(61)

    var targetAngle = minAngle

    val servo = HardwareMap.hood(range = Range.Default)

    override val components = listOf(servo)

    override fun update(deltaTime: Double) {
        log("pos") value servo.position
        log("angle (deg)") value targetAngle * 180 / PI
        servo.position = (
            0.87 - (
                ( targetAngle - minAngle ) * (0.87 - 0.59)
                / (maxAngle - minAngle)
            )
        )
    }

    fun setAngle(angle: Double) = setAngle { angle } until { true }
    fun setAngle(angle: () -> Double) = run {
        targetAngle = angle()
    } withInit { targetAngle = angle() }

    fun down() = setAngle(minAngle)

}
