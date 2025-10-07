package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.P
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.F
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.MAX_VEL
import org.firstinspires.ftc.teamcode.subsystem.Shooter.getVelNoHood
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.abs
import kotlin.math.exp

@Config
object FlywheelConfig {
    @JvmField var P = 4.05
    @JvmField var D = 0.0
    @JvmField var F = 0.57
    @JvmField var MAX_VEL = 253.0
}


object Flywheel: Subsystem<Flywheel>(), Tunable<DoubleState> {
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

    //@TunablePIDF(0.0, MAX_VEL)
    val controller = PIDFController(
        P = { P },
        D = { D },
        targetPosition = 0.0,
        pos = { this@Flywheel.velocity },
        setpointError = { targetPosition - pos() },
        apply = { motor.compPower(it) },
    )
    override val components = listOf(motor)

    val readyToShoot get() = abs(controller.error) < 0.05


    init {
        motor.useEncoder(HardwareMap.shooterEncoder(FORWARD, 1.0))
        motor.encoder!!.inPerTick =  - 1.0 / 2350
        controller.F = { targetPosition: Double, effort: Double ->
            exp( (targetPosition - 1) / F)
        } // voltage ff based on velocity vs voltage regression
    }

    override fun update(deltaTime: Double) {
        //controller.updateController(deltaTime)
        log("velocity") value velocity
        //log("non smoothed") value motor.rawVel
        log("target") value controller.targetPosition
        log("voltage") value (motor.lastWrite or 0.0)
        log("ready to shoot") value readyToShoot

        log("controller/error") value controller.setpointError.invoke(controller)
        log("controller/pos") value controller.pos()
        log("controller/targetPosition") value controller.targetPosition
        log("controller/feedback") value controller.feedback
        log("controller/P") value controller.P()
        log("controller/F") value controller.F(controller.targetPosition, 0.0)
        log("usingFeedback") value usingFeedback

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
        this.controller.targetPosition = velocity().toDouble() / MAX_VEL
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    }

    fun runAtVelocity(velocity: Double) = runAtVelocity { velocity }

    fun shootingState(dist: () -> Double) =  (
        runAtVelocity { getVelNoHood(dist()) }
    ) withEnd stop()
}