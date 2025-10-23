package org.firstinspires.ftc.teamcode.command

import androidx.core.util.Supplier
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector3D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.div

import kotlin.math.atan
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.times
import kotlin.unaryMinus

class ShootingState(val fromPos: Supplier<Pose2D>) : Command() {

    override val requirements = mutableSetOf<Subsystem<*>>(Hood, Flywheel)

    //the coordinates for the blue goal in inches.
    //the origin is at the center of the field.
    var target_pt = Vector3D(-64,64,31)

    //coordinate shift from the robots origin to the
    //approximate place the ball starts its parabolic path.
    var flywheel_offset = Vector2D(0.5,14)

    //gravity measured in in/sec^2
    var gravity = 386

    override fun initialize() {
        /** Using feedback sets the PID controller active. */
        Flywheel.usingFeedback = true
    }

    /**
     * This function is the basic trajectory function found on the trajectory Wikipedia page.
     * @param launchAngle The angle of the projectile launch.
     * @param initialVelocity The initial velocity of the projectile.
     * @param groundTravel The horizontal distance traveled by the projectile.
     * @return The height of the projectile at the given ground travel distance.
     */
    private fun trajectory(
        launchAngle: Double,
        initialVelocity: Double,
        groundTravel: Double
    ): Double {
        return groundTravel * tan(launchAngle) - (gravity * groundTravel.pow(2)) / (2 * initialVelocity.pow(
            2
        ) * cos(launchAngle).pow(2))
    }

    /**
     * Calculates the derivative of the trajectory function with respect to the launch angle.
     * @param launchAngle The angle of the projectile launch.
     * @param initialVelocity The initial velocity of the projectile.
     * @param groundTravel The horizontal distance traveled by the projectile.
     * @return The derivative of the trajectory height.
     */
    private fun trajectoryDirective(
        launchAngle: Double,
        initialVelocity: Double,
        groundTravel: Double
    ): Double {
        return -2 * groundTravel.pow(2) * cos(launchAngle).pow(2) * tan(launchAngle) + 2 * gravity * groundTravel
    }

    /**
     * A reformed version of the getHeight function to calculate the required initial velocity.
     * @param launchAngle The chosen launch angle.
     * @param targetPoint The desired target coordinates in a 2D plane.
     * @return The initial velocity required to hit the target point.
     */
    private fun getInitVelocity(launchAngle: Double, targetPoint: Vector2D): Double {
        return sqrt(
            -(gravity * targetPoint.x.pow(2)) / (2 * cos(launchAngle).pow(2) * targetPoint.y - targetPoint.x * tan(
                launchAngle
            ) * 2 * cos(launchAngle).pow(2))
        )
    }

    override fun execute() {

        var from_pos = fromPos.get()


        /**
         * Compute the point of the target with the flywheel at (0,0) and the target
         * all laying on a 2d plane.
         */
        var target_point_2d = Vector2D(
            (Globals.goalPose - from_pos).mag - flywheel_offset.x,
            31-flywheel_offset.y
            )

        log("target") value doubleArrayOf(target_point_2d.x, target_point_2d.y)

        var through_point_2d = Vector2D(target_pt.x-5,target_pt.y+5)

        /**
         * Compute the velocity to pass through both target point and through point.
         * This is using a system of equations that is just the getInitVelocity but with the
         * target point for one of them, and the through point for the other.
         */
        var velocity = atan(
            -(
                through_point_2d.x.pow(2) * target_point_2d.y
                - target_point_2d.x.pow(2) * through_point_2d.y
            )/(
                through_point_2d.x * target_point_2d.x.pow(2)
                - target_point_2d.x * through_point_2d.x.pow(2)
            )
        )

        log("velocity") value velocity
        /**
         * With that velocity, compute the launch angle to get to the target
         * using Newton's method.
         */
        var prevAngle = PI/4
        var closeness = trajectoryMath.trajectory(prevAngle, velocity, target_point_2d.x)-target_point_2d.y
        var threshold = 1
        var newAngle = 0.0
        var loopsLeft = 10
        var angles = arrayListOf<Double>()
        var F_s = arrayListOf<Double>()
        var F_deriv_s = arrayListOf<Double>()

        while (closeness > threshold && loopsLeft > 0){
            val F = trajectory(prevAngle, velocity, target_point_2d.x) -target_point_2d.y
            val F_deriv = trajectoryDirective(prevAngle,velocity,target_point_2d.x)

            newAngle = prevAngle - F / F_deriv

            F_s.add(F)
            F_deriv_s.add(F_deriv)

            closeness = trajectory(prevAngle, velocity, target_point_2d.x)-target_point_2d.y
            prevAngle = newAngle
            loopsLeft --
            angles.add(newAngle)
        }
        var launchAngle = newAngle

        log("newtons method F") value F_s.toDoubleArray()
        log("newtons method F'") value F_deriv_s.toDoubleArray()
        log("loop overrun!") value (loopsLeft <= 0)
        log("launch angle") value launchAngle
        log("angles") value angles.toDoubleArray()

        /** Set flywheel controller setpoints. */
        Flywheel.targetVelocity = velocity/FlywheelConfig.MAX_VEL
        Hood.setAngle { launchAngle }.execute()
    }

    override fun end(interrupted: Boolean){
        /**
         * Command flywheels to stop using feedback control.
         * Set flywheel power to 0 and hood angle to 0.
         */
        Flywheel.usingFeedback = false
        Flywheel.motors.forEach { it.power = 0.0 }
        Hood.setAngle(0.0)
    }

    override var name = { "ShootingState" }
}
