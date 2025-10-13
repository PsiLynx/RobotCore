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
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Config
object FlywheelConfig {
    @JvmField var P = 4.05
    @JvmField var D = 0.0
    @JvmField var F = 0.57
    @JvmField var MAX_VEL = 253.0
}


object Flywheel: Subsystem<Flywheel>(), Tunable<DoubleState> {
    const val phiNoHood = 0.20944 // 12deg in rad

    val velocity get() = motor.velocity
    val acceleration get() = motor.acceleration

    var targetVelocity
        get() = controller.targetPosition
        set(value) { controller.targetPosition = value }

    var usingFeedback = false

    override val tuningForward = DoubleState(1.0)
    override val tuningBack = DoubleState(0.0)
    override val tuningCommand = { it: State<*> ->
        runAtVelocity((it as DoubleState).value)
    }

    private val motor = HardwareMap.shooter(
        REVERSE,
        lowPassDampening = 0.5
    )

    private val controller = PIDFController(
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
        log("velocity") value velocity
        log("controller") value controller
        log("voltage") value (motor.lastWrite or 0.0)
        log("ready to shoot") value readyToShoot
        log("usingFeedback") value usingFeedback

        log("controller") value controller


        if(usingFeedback) controller.updateController(deltaTime)


    }

    fun getVelNoHood(dist: Double): Double {

        val start  = Vector2D(cos(phiNoHood + PI/2), sin(phiNoHood + PI/2))
        val target = Vector2D(dist, 38)
        val l = target - start
        log("l") value l
        log("start") value start
        log("target") value target
        log("numerator") value ( 9.82 * l.x.pow(2) )
        log("denominator") value
                ( l.x * sin(2*phiNoHood) - 2*l.y*cos(phiNoHood)*cos(phiNoHood))

        return sqrt(
            ( 386.088 * l.x.pow(2) )
                    / ( l.x * sin(2*phiNoHood) - 2*l.y*cos(phiNoHood)*cos(phiNoHood))
        )
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
        this.targetVelocity = velocity().toDouble() / MAX_VEL
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    }

    fun runAtVelocity(velocity: Double) = runAtVelocity { velocity }

    fun shootingState(dist: () -> Double) =  (
        runAtVelocity { getVelNoHood(dist()) }
    ) withEnd stop()
}