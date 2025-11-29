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
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.I
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
    @JvmField var P = 5.0
    @JvmField var I = 0.0
    @JvmField var D = 2.0
    @JvmField var F = 0.98
}


object Flywheel: Subsystem<Flywheel>(), Tunable<DoubleState> {
    const val phiNoHood = PI / 2 - 0.20944 // 12deg in rad

    val velocity get() = motor.velocity
    val acceleration get() = motor.acceleration

    /**
     * target exit velocity of the artifact, in in/s
     */
    var targetVelocity
        get() = rotationalVelToLinearVel(
            controller.targetPosition
        )
        set(value) {
            controller.targetPosition = linearVelToRotationalVel(
                value
            ).coerceIn(-1.0, 1.0)
        }

    var usingFeedback = false

    val running get() = abs(motor.power) > 0.01

    private const val REGRESSION_A = 0.0
    private const val REGRESSION_B = 230.0
    private const val REGRESSION_C = 0.0
    /**
     * convert rotational speed (fraction of max) to linear artifact exit vel
     * @param w rotational speed as a fraction of the maximum rotational speed
     * @return linear speed of the artifact exit, in/s
     */
    private fun rotationalVelToLinearVel(w: Double) =
        REGRESSION_A * w.pow(2) + REGRESSION_B * w + REGRESSION_C

    /**
     * convert linear artifact exit vel to rotational speed (fraction of max)
     * @param v linear speed of the artifact exit, in/s
     * @return rotational speed as a fraction of the maximum rotational speed
     */
    private fun linearVelToRotationalVel(v: Double) = (
        ( v - REGRESSION_C ) / REGRESSION_B
    )

    override val tuningForward = DoubleState(1.0)
    override val tuningBack = DoubleState(0.0)
    override val tuningCommand = { it: State<*> ->
        runAtVelocity((it as DoubleState).value)
    }

    private val motor = HardwareMap.shooter(
        REVERSE,
        lowPassDampening = 0.5
    )

    val controller = PIDFController(
        P = { P },
        I = { I },
        D = { D },
        targetPosition = 0.0,
        pos = { this@Flywheel.velocity },
        setpointError = { targetPosition - pos() },
        apply = { motor.compPower(it) },
    )
    override val components = listOf(motor)

    val readyToShoot get() = abs(controller.error) < 0.1 && usingFeedback



    init {
        motor.useEncoder(HardwareMap.shooterEncoder(FORWARD, 1.0))
        motor.encoder!!.inPerTick =  - 1.0 / 2240
        controller.F = { targetPosition: Double, effort: Double ->
            (targetPosition * F)
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

    //returns the velocity vector of the ball with no hood extention.
    fun getVelNoHood(dist: Double): Double {

        val start  = Vector2D(cos(phiNoHood + PI/2), sin(phiNoHood + PI/2))
        val target = Vector2D(dist, 38)
        val l = target - start
        log("l") value l
        log("start") value start
        log("target") value target
        log("numerator") value ( 386.088 * l.x.pow(2) )
        log("denominator") value
                ( l.x * sin(2*phiNoHood) - 2*l.y*cos(phiNoHood)*cos(phiNoHood))

        return sqrt(
            ( 386.088 * l.x.pow(2) )
            / ( l.x * sin(2*phiNoHood) - 2*l.y*(cos(phiNoHood).pow(2)))
        )
    }
    fun getVel(phi: Double, dist: Double): Double {

        val start  = Vector2D(cos(phi + PI/2), sin(phi + PI/2))
        val target = Vector2D(dist, 33 - 12)
        val l = target - start
        log("l") value l
        log("start") value start
        log("target") value target
        log("numerator") value ( 386.088 * l.x.pow(2) )
        log("denominator") value
                ( l.x * sin(2*phi) - 2*l.y*cos(phi)*cos(phi))

        return sqrt(
            ( 386.088 * l.x.pow(2) )
            / ( l.x * sin(2*phi) - 2*l.y*(cos(phi).pow(2)))
        )
    }
    //sets the motor to full maximum power
    fun fullSend() = setPower(1.0)

    //sets the flywheel to no power
    fun stop() = runOnce {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    }

    fun setPower(power: Double) = run {
        motor.compPower(power)
    } withEnd { motor.power = 0.0 }

    fun runAtVelocity(velocity: () -> Double) = run {
        usingFeedback = true
        this.targetVelocity = velocity().toDouble()
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    }

    fun runAtVelocity(velocity: Double) = runAtVelocity { velocity }

    fun shootingState(dist: () -> Double) =  (
        runAtVelocity { getVelNoHood(dist()) }
    ) withEnd stop()
}
