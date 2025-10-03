package org.firstinspires.ftc.teamcode.subsystem

import android.R.attr.left
import android.R.attr.x
import android.R.attr.y
import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.P
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.controller.pid.TunablePIDF
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.exp

@Config
object FlywheelConfig {
    @JvmField var P = 0.001
    @JvmField var D = 0.0
}


object Flywheel: Subsystem<Flywheel>(), Tunable<DoubleState> {
    const val MAX_VEL = 253.3
    val velocity get() = motor.velocity
    val acceleration get() = motor.acceleration

    var usingFeedback = false

    override val tuningForward = DoubleState(1.0)
    override val tuningBack = DoubleState(0.0)
    override val tuningCommand = { it: State<*> ->
        runAtVelocity((it as DoubleState).value)
    }

    val motor = HardwareMap.shooter(
        REVERSE,
        lowPassDampening = 0.5
    )

    @TunablePIDF(0.0, MAX_VEL)
    val controller = PIDFController(
        P = { P },
        D = { D },
        targetPosition = 0.0,
        pos = { this@Flywheel.velocity },
        setpointError = { targetPosition - pos() },
        apply = { motor.compPower(it) },
    )
    override val components = listOf(motor)

    init {
        motor.useEncoder(HardwareMap.shooterEncoder(FORWARD, 1.0))
        motor.encoder!!.inPerTick =  - 253.3 / 2350
        controller.F = { targetPosition: Double, effort: Double ->
            exp( (targetPosition - 264.56459) / 181.00342)
        } // voltage ff based on velocity vs voltage regression
    }

    override fun update(deltaTime: Double) {
        //controller.updateController(deltaTime)
        log("velocity") value velocity
        //log("non smoothed") value motor.rawVel
        log("target") value controller.targetPosition
        log("voltage") value (motor.lastWrite or 0.0)

        log("controller/error") value controller.setpointError.invoke(controller)
        log("controller/pos") value controller.pos()
        log("controller/targetPosition") value controller.targetPosition
        log("controller/feedback") value controller.feedback
        log("controller/P") value controller.P()

        if(usingFeedback) controller.updateController(deltaTime)
    }
    fun fullSend() = setPower(1.0)
    fun stop() = runOnce {
        motors.forEach { it.power = 0.0 }
    }

    fun setPower(power: Double) = run {
        motor.compPower(power)
    } withEnd { motor.power = 0.0 }

    fun runAtVelocity(velocity: () -> Double) = run {
        usingFeedback = true
        this.controller.targetPosition = velocity().toDouble()
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    }

    fun runAtVelocity(velocity: Double) = run {
        usingFeedback = true
        this.controller.targetPosition = velocity.toDouble()
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    }

}