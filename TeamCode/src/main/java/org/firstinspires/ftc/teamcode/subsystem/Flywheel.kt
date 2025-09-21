package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.P
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
@Config
object FlywheelConfig {
    @JvmField var P = 0.0
    @JvmField var D = 0.0
}


object Flywheel: Subsystem<Flywheel>(), Tunable<DoubleState> {
    val velocity get() = motor.velocity
    val acceleration get() = motor.acceleration

    override val tuningForward = DoubleState(1.0)
    override val tuningBack = DoubleState(0.0)
    override val tuningCommand = { it: State<*> ->
        runAtVelocity((it as DoubleState).value)
    }

    val motor = HardwareMap.shooter(FORWARD)
    val controller = PIDFController(
        P = { P },
        D = { D },
        targetPosition = 0.0,
        pos = { this@Flywheel.velocity },
        apply = { motor.compPower(it) },
        setpointError = { targetPosition - pos() },
    )
    override val components = listOf(motor)

    init {
        motor.useEncoder(HardwareMap.shooterEncoder(FORWARD, 1.0))
    }

    override fun update(deltaTime: Double) {
        controller.updateController(deltaTime)
    }

    fun runAtVelocity(velocity: () -> Double) = run {
        this.controller.targetPosition = velocity().toDouble()
    } withEnd { motors.forEach { it.power = 0.0 } }

    fun runAtVelocity(velocity: Double) = run {
        this.controller.targetPosition = velocity.toDouble()
    } withEnd { motors.forEach { it.power = 0.0 } }

}