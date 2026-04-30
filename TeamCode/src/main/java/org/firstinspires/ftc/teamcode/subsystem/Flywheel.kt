package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.P
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.Ka
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.Ks
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.MAX_VEL
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Config
object FlywheelConfig {
    @JvmField var P = 6.0
    @JvmField var D = 0.5
    @JvmField var Ka = 0.0
    @JvmField var Ks = 0.0
    @JvmField var MAX_VEL = 450.0
}


object Flywheel: Subsystem<Flywheel>() {
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
         MAX_VEL * w

    /**
     * convert linear artifact exit vel to rotational speed (fraction of max)
     * @param v linear speed of the artifact exit, in/s
     * @param theta hood angle (theta=0 is horizontal)
     * @return rotational speed as a fraction of the maximum rotational speed
     */
    private fun linearVelToRotationalVel(v: Double) = (
        v / MAX_VEL
    )

    val motorLeft = HardwareMap.shooterLeft(
        FORWARD,
        lowPassDampening = 0.5
    )
    val motorRight = HardwareMap.shooterRight(
        REVERSE,
        lowPassDampening = 0.5
    )

    init {
        motorLeft.encoder = HardwareMap.shooterEncoder(REVERSE, 1.0)
        motorLeft.encoder!!.inPerTick = 1.0 / 2600
    }

    override val components = listOf(motorLeft, motorRight)

    val readyToShoot get() = abs(linearVelToRotationalVel(
        currentState.velocity.toDouble()
        - targetState.velocity.toDouble()
    )) < 0.04 && usingFeedback


    override fun update(deltaTime: Double) {
        if(currentState.acceleration.toDouble() > 0) recovered = true

        if(
            justShot == false
            && recovered
            && currentState.acceleration.toDouble() < -300.0
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
                }
                else if (velErr > -0.05) it.compPower(
                    linearVelToRotationalVel(
                        targetState.velocity.toDouble()
                    ) * (1 - Ks ) + Ks
                )
                else it.compPower(0.0)

                 /*
                it.compPower(
                    VaState(
                        linearVelToRotationalVel(
                            targetState.velocity.toDouble()
                            - currentState.velocity.toDouble()
                        ),
                        linearVelToRotationalVel(
                            currentState.acceleration.toDouble()
                        )
                    ).applyPD(P, D).toDouble()
                    + (
                        linearVelToRotationalVel(
                            targetState.velocity.toDouble()
                        ) * (1 - Ks ) + Ks
                    )
                    + ( targetState.acceleration * Ka ).toDouble()
                )
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

    fun fullSend() = setPower(1.0)

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

}
