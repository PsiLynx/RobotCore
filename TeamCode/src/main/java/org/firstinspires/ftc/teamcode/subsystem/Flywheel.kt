package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.P
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.F
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.I
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.REGRESSION_A
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.REGRESSION_B
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
    @JvmField var P = 3.0
    @JvmField var I = 0.0
    @JvmField var D = 0.0
    @JvmField var F = 1.0
    @JvmField var REGRESSION_A = 300.0
    @JvmField var REGRESSION_B = 0.0
}


object Flywheel: Subsystem<Flywheel>(), Tunable<DoubleState> {
    const val phiNoHood = PI / 2 - 0.20944 // 12deg in rad

    /**
     * current exit velocity of the articaft, in/s
     */
    val currentState get() = VaState(
        rotationalVelToLinearVel(motorLeft.velocity),
        rotationalVelToLinearVel(motorLeft.acceleration)
    )

    /**
     * target exit velocity of the artifact, in/s
     */
    var targetState = VaState(0.0, 0.0)

    var justShot = false
    private set
    var recovered = true

    var usingFeedback = false

    val running get() = abs(motorLeft.power) > 0.01

    /**
     * convert rotational speed (fraction of max) to linear artifact exit vel
     * @param w rotational speed as a fraction of the maximum rotational speed
     * @param theta hood angle (theta=0 is horizontal)
     * speed
     * @return linear speed of the artifact exit, in/s
     */
    private fun rotationalVelToLinearVel(w: Double) =
         REGRESSION_A * w

    /**
     * convert linear artifact exit vel to rotational speed (fraction of max)
     * @param v linear speed of the artifact exit, in/s
     * @param theta hood angle (theta=0 is horizontal)
     * @return rotational speed as a fraction of the maximum rotational speed
     */
    private fun linearVelToRotationalVel(v: Double) = (
        v / REGRESSION_A
    )

    override val tuningForward = DoubleState(1.0)
    override val tuningBack = DoubleState(0.0)
    override val tuningCommand = { it: State<*> ->
        runAtVelocity((it as DoubleState).value)
    }

    val motorLeft = HardwareMap.shooterLeft(
        FORWARD,
        lowPassDampening = 0.5
    )
    val motorRight = HardwareMap.shooterRight(
        REVERSE,
        lowPassDampening = 0.5
    )

    override val components = listOf(motorLeft, motorRight)

    val readyToShoot get() = abs(
        currentState.velocity.toDouble()
        - targetState.velocity.toDouble()
    ) < 0.04 && usingFeedback

    init {
        motorLeft.useEncoder(HardwareMap.shooterEncoder(FORWARD, 1.0))
        motorLeft.encoder!!.inPerTick = 1.0 / 2600
    }

    override fun update(deltaTime: Double) {
        if(currentState.acceleration.toDouble() > 0) recovered = true

        if(
            justShot == false
            && recovered
            && currentState.acceleration.toDouble() < -1.0
        ) {
            justShot = true
            recovered = false
        }
        else justShot = false

        if(usingFeedback){
            val velErr = linearVelToRotationalVel(
                targetState.velocity.toDouble()
                - currentState.velocity.toDouble()
            )
            motors.forEach {
                if (velErr > 0.01) {
                    it.compPower(1.0)
                } else if (velErr > -0.05) it.compPower(
                    F * linearVelToRotationalVel(
                        targetState.velocity
                        .toDouble()
                    )
                ) else it.compPower(0.0)
                /*
                it.power = VaState(

                    linearVelToRotationalVel(
                        targetState.velocity.toDouble()
                        - currentState.velocity.toDouble()
                    ),

                    linearVelToRotationalVel(
                        - targetState.acceleration.toDouble()
                        + currentState.acceleration.toDouble()
                    )

                ).applyPD(P, D).toDouble() + (
                    linearVelToRotationalVel(
                        targetState.velocity.toDouble()
                    ) * F
                ).toDouble()
                 */
            }
        }
        log("velocity") value currentState.velocity
        log("velocity ticks") value motorLeft.encoder!!.velSupplier(0.0)
        log("acceleration") value currentState.acceleration
        log("voltage") value (motorLeft.lastWrite or 0.0)
        log("ready to shoot") value readyToShoot
        log("usingFeedback") value usingFeedback
        log("justShot") value justShot
        log("recovered") value recovered
        log("target vel") value targetState.velocity.toDouble()
    }

    //returns the velocity vector of the ball with no hood extention.
    fun getVelNoHood(dist: Double): Double {

        val start  = Vector2D(cos(phiNoHood + PI/2), sin(phiNoHood + PI/2))
        val target = Vector2D(dist, 38)
        val l = target - start
        log("l") value l
        log("start") value start
        log("targetState") value target
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
        log("targetState") value target
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
        motorLeft.compPower(power)
        motorRight.compPower(power)
        usingFeedback = true
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    } withName "Fw: setPower"

    fun runAtVelocity(velocity: () -> Double) = run {
        usingFeedback = true
        this.targetState = VaState(velocity().toDouble(), 0.0)
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    } withName "Fw: runAtVel"

    fun runAtVelocity(velocity: Double) = runAtVelocity { velocity }

    fun shootingState(dist: () -> Double) =  (
        runAtVelocity { getVelNoHood(dist()) }
    ) withEnd stop()
}
