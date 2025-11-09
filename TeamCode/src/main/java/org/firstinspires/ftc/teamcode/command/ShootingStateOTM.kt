package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Pinpoint
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.log

import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sin

/**
 * This class is responsible for settings the flywheel speed, hood angle, and turret angle
 * accounting for the motion of the robot.
 * math graphs can be found at https://www.desmos.com/calculator/jaxgormzj1
 */
class ShootingStateOTM(
    var from_pos: () -> Vector2D,
    var velocity: () -> Pose2D,
    var throughPointOffset: Vector2D = Vector2D(-17, 15),
    val target: Vector3D = Globals.goalPose
) : Command() {

    override val requirements = mutableSetOf<Subsystem<*>>(Hood, Flywheel, Turret)

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
    private fun getInitVelocity(
        launchAngle: Double,
        targetPoint: Vector2D,
        fromPos: Vector2D
    ): Double {
        return sqrt(
            -(
                    gravity* ( targetPoint - fromPos).x.pow(2))
                    /(
                    2*cos(launchAngle).pow(2)*( targetPoint - fromPos).y
                            -( targetPoint - fromPos).x*tan(launchAngle)*2*cos(launchAngle).pow(2)
                    )
        )
    }

    override fun execute() {
        /**
         * Compute the point of the target with the flywheel at (0,0) and the target
         * all laying on a 2d plane.
         * Uses the Pythagorean formula for computing x.
         */
        var target_point_2d = Vector2D(
            (target.groundPlane - from_pos()).mag - Globals.flywheelOffset.x,
            target.z - Globals.flywheelOffset.y
        )

        var through_point_2d = target_point_2d+throughPointOffset

        /**
         * Compute the velocity to pass through both target point and through point.
         * This is using a system of equations that is just the getInitVelocity but with the
         * target point for one of them, and the through point for the other.
         */
        var launchAngle = atan(-(through_point_2d.x.pow(2) * target_point_2d.y - target_point_2d.x.pow(2) * through_point_2d.y)/(through_point_2d.x * target_point_2d.x.pow(2) - target_point_2d.x * through_point_2d.x.pow(2)))
        if(launchAngle > PI/2 - Hood.minAngle) {
            launchAngle = PI/2 - Hood.minAngle
        }

        /** Set flywheel controller setpoints. */
        var velocity = getInitVelocity(
            launchAngle,
            target_point_2d,
            Vector2D(
                -(2.83 + 5)/2,
                0
            ) rotatedBy Rotation2D( launchAngle + PI/2 )
        )

        //turn the calculated data into a field-centric 3d launch vector to
        //calculate the trajectory accounting for robot motion.

        //calculate the field centric angle to the goal:

        var angleToGoal = atan2(target.y - from_pos().y, target.x - from_pos().x)

        println("angleToGoal $angleToGoal")

        //calculate the sides of the triangle baised from angle and hyp length.

        var vecX = sin(angleToGoal) * velocity
        var vecY = cos(angleToGoal) * velocity
        var vecZ = sin(launchAngle) * velocity

        println("sin $vecX")
        println("cos $vecY")
        println("vecZ $vecZ")

        var launchVec = Vector3D(vecX, vecY, vecZ)

        println("launchVec1 $launchVec")

        //adjust for the motion of the drive base

        launchVec = launchVec - Vector3D(velocity().x, velocity().y,0)
        println("launchVec2 $launchVec")
        println("drivetrein ${Drivetrain.velocity}")
        println("test ${Vector3D(5,5,5)- Vector3D(velocity().x, velocity().y,0)}")

        //now parse and command the flywheel, hood, and turret.
        Flywheel.targetVelocity = launchVec.mag
        Hood.targetAngle = launchVec.verticalAngle.toDouble()
        Turret.setAngle { launchVec.horizontalAngle }

        log("targetVelocity") value velocity
        log("launchAngle") value launchAngle
        log("target_point") value target_point_2d

        log("launchVec") value launchVec
        log("FlywheelVelocityWithRBmotion") value Flywheel.velocity
        log("launchAngleWithRBmotion") value Hood.targetAngle
        log("launchHeadingWithRBmotion") value Turret.angle

        //debug printouts
        println("Velocity ${launchVec.mag}")
        println("launch Angle ${launchVec.verticalAngle.toDouble()}")
        println("launch Heading W Motion ${launchVec.horizontalAngle}")
        println("\nreading from the hardware drivers:\n")
        println("Velocity W motion ${Flywheel.targetVelocity}")
        println("launch Angle W motion ${Hood.targetAngle}")
        println("launch Heading W Motion ${Turret.angle}")

    }

    override fun end(interrupted: Boolean){
        /**
         * Command flywheels to stop using feedback control.
         * Set flywheel power to 0 and hood angle to 0.
         */
        Flywheel.usingFeedback = false
        Flywheel.motors.forEach { it.power = 0.0 }
        Hood.setAngle(Hood.minAngle)
    }

    override var name = { "ShootingState" }
}