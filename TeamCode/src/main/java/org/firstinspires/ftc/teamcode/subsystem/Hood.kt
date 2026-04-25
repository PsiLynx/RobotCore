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
import org.psilynx.psikit.core.mechanism.LoggedMechanism2d
import org.psilynx.psikit.core.mechanism.LoggedMechanismLigament2d
import org.psilynx.psikit.core.wpi.Color8Bit
import kotlin.math.PI
import kotlin.math.min

object Hood: Subsystem<Hood>(), Tunable<DoubleState> {
    override val tuningBack = DoubleState(0.0)
    override val tuningForward = DoubleState(1.0)
    override val tuningCommand = { it: State<*> ->
        setAngle((it as DoubleState).value) as Command
    }

    val minAngle = degrees(28)
    val maxAngle = degrees(51)

    var targetAngle = minAngle
        set(value){
            field = (
                if(value > maxAngle) maxAngle
                else if(value < minAngle) minAngle
                else value
            )
        }

    val servo = HardwareMap.hood(range = Range.Default)

    val mechanism = LoggedMechanism2d(0.5, 0.5)
    init {
        mechanism.getRoot("hood", 0.15, 0.1)
        mechanism.setBackgroundColor(Color8Bit("#000000"))
        mechanism.root.append(LoggedMechanismLigament2d(
            "hood", 0.2, 0.0, 2.0, Color8Bit("#FFA500")
        ))
    }

    override val components = listOf(servo)

    override fun update(deltaTime: Double) {
        (
            mechanism.root.objects().first() as LoggedMechanismLigament2d
        ).angle = (targetAngle) * 180 / PI

        log("pos") value servo.position
        log("angle (deg)") value targetAngle * 180 / PI
        log("mechanism") value mechanism
        servo.position = (
            0.08 + (
                ( targetAngle - minAngle ) * (0.95 - 0.08)
                / (maxAngle - minAngle)
            )
        )
    }

    fun setAngle(angle: Double) = setAngle { angle } until { true }
    fun setAngle(angle: () -> Double) = run {
        targetAngle = angle()
    } withInit { targetAngle = angle() } withName "Hd: setAngle"

    fun down() = setAngle(minAngle)

}
