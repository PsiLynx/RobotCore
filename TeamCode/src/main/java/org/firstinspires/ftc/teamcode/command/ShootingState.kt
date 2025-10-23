package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D

import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.pow

class ShootingState() : Command() {

    override val requirements = mutableSetOf<Subsystem<*>>(Hood, Flywheel, Drivetrain)
    var target_pt = Vector3D(100,100,100)
    var flywheel_offset = Vector2D(-20,20)

    var gravity = 336

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
    private fun trajectory(launchAngle: Double, initialVelocity: Double, groundTravel: Double): Double {
        return groundTravel*tan(launchAngle) - (gravity * groundTravel.pow(2))/(2*initialVelocity.pow(2)*cos(launchAngle).pow(2))
    }

    /**
     * Calculates the derivative of the trajectory function with respect to the launch angle.
     * @param launchAngle The angle of the projectile launch.
     * @param initialVelocity The initial velocity of the projectile.
     * @param groundTravel The horizontal distance traveled by the projectile.
     * @return The derivative of the trajectory height.
     */
    private fun trajectoryDirective(launchAngle: Double, initialVelocity: Double, groundTravel: Double): Double {
        return -2 * groundTravel.pow(2) * cos(launchAngle).pow(2) * tan(launchAngle) + 2 * gravity * groundTravel
    }

    /**
     * A reformed version of the getHeight function to calculate the required initial velocity.
     * @param launchAngle The chosen launch angle.
     * @param targetPoint The desired target coordinates in a 2D plane.
     * @return The initial velocity required to hit the target point.
     */
    private fun getInitVelocity(launchAngle: Double, targetPoint: Vector2D): Double {
        return sqrt(-(gravity*targetPoint.x.pow(2))/(2*cos(launchAngle).pow(2)*targetPoint.y-targetPoint.x*tan(launchAngle)*2*cos(launchAngle).pow(2)))
    }

    override fun execute() {

        var from_pos = Drivetrain.position

        /**
         * Compute the point of the target with the flywheel at (0,0) and the target
         * all laying on a 2d plane.
         * Uses the Pythagorean formula for computing x.
         */
        var target_point_2d = Vector2D(
            sqrt(target_pt.x.pow(2)+target_pt.y.pow(2))-from_pos.x+flywheel_offset.x,
            tan(target_pt.verticalAngle.toDouble())*target_pt.x.pow(2)+target_pt.y.pow(2)-from_pos.y+flywheel_offset.y
        )
        var through_point_2d = Vector2D(target_pt.x-5,target_pt.y+5)

        /**
         * Compute the velocity to pass through both target point and through point.
         * This is using a system of equations that is just the getInitVelocity but with the
         * target point for one of them, and the through point for the other.
         */
        var velocity = atan(-(through_point_2d.x.pow(2) * target_point_2d.y - target_point_2d.x.pow(2) * through_point_2d.y)/(through_point_2d.x * target_point_2d.x.pow(2) - target_point_2d.x * through_point_2d.x.pow(2)))

        /**
         * With that velocity, compute the launch angle to get to the target
         * using Newton's method.
         */
        var prevAngle = PI/4
        var closeness = trajectory(prevAngle, velocity, target_point_2d.x)-target_point_2d.y
        var threshold = 0.1
        var newAngle = 0.0
        while (closeness > threshold){
            newAngle = prevAngle - (trajectory(prevAngle, velocity, target_point_2d.x)-target_point_2d.y) / trajectoryDirective(prevAngle,velocity,target_point_2d.x)
            closeness = trajectory(prevAngle, velocity, target_point_2d.x)-target_point_2d.y
            prevAngle = newAngle
        }
        var launchAngle = newAngle

        /** Set flywheel controller setpoints. */
        Flywheel.targetVelocity = velocity/FlywheelConfig.MAX_VEL
        Hood.setAngle { launchAngle }
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
