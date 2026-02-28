package org.firstinspires.ftc.teamcode.shooter

import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.Hood
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * This class is responcible for conroling the flywheel speed and the hood angle
 * math graphs can be found at https://www.desmos.com/calculator/jaxgormzj1
 */
object ComputeTraj {

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
        groundTravel: Double,
        gravity: Double = 386.0
    ): Double {
        return groundTravel * tan(launchAngle) - (gravity * groundTravel.pow(2)) /
                (2 * initialVelocity.pow(2) * cos(launchAngle).pow(2))
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
        groundTravel: Double,
        gravity: Double = 386.0
    ): Double {
        return -2 * groundTravel.pow(2) * cos(launchAngle).pow(2) * tan(launchAngle) + 2 * gravity * groundTravel
    }

    /**
     * A reformed version of the getHeight function to calculate the required initial velocity.
     * @param launchAngle The chosen launch angle.
     * @param targetPoint The desired targetState coordinates in a 2D plane.
     * @return The initial velocity required to hit the targetState point.
     */
    private fun getInitVelocity(
        launchAngle: Double,
        targetPoint: Vector2D,
        fromPos: Vector2D,
        gravity: Double = 386.0
    ): Double {
        return sqrt(
            -(
                    gravity * (targetPoint - fromPos).x.pow(2))
                    / (
                    2 * cos(launchAngle).pow(2) * (targetPoint - fromPos).y
                            - (targetPoint - fromPos).x * tan(launchAngle) * 2 * cos(launchAngle).pow(
                        2
                    )
                    )
        )
    }

    fun computeTraj(
        throughPoint2D: Vector2D,
        targetPoint: Vector2D,
    ): Pair<Double, Double> {

        /**
         * Compute the velocity to pass through both targetState point and through point.
         * This is using a system of equations that is just the getInitVelocity but with the
         * targetState point for one of them, and the through point for the other.
         */
        var launchAngle = atan(
            -(throughPoint2D.x.pow(2) * targetPoint.y - targetPoint.x.pow(2) * throughPoint2D.y) /
                    (throughPoint2D.x * targetPoint.x.pow(2) - targetPoint.x * throughPoint2D.x.pow(
                        2
                    ))
        )
        if (launchAngle > PI / 2 - Hood.minAngle) {
            launchAngle = PI / 2 - Hood.minAngle
        }
        if (launchAngle < PI / 2 - Hood.maxAngle) {
            launchAngle = PI / 2 - Hood.maxAngle
        }

        /** Set flywheel controller setpoints. */
        var velocity = getInitVelocity(
            launchAngle,
            targetPoint,
            Vector2D(0, 0)
        )

        return Pair(velocity, launchAngle)
    }

    fun compLaunchVec(goal: Vector3D, myPos: Pose2D, botVel: Pose2D): Vector3D{

        /**
         * compute the 2d path of the ball.
         */

        /**
         * Compute the point of the targetState with the flywheel at (0,0) and the targetState
         * all laying on a 2d plane.
         * Uses the Pythagorean formula for computing x.
         */

        val shooterOffset = (
                ShooterConfig.flywheelOffset.groundPlane
                        rotatedBy myPos.heading
                )

        //println("shooter Pos$shooterOffset")

        val targetPoint2D = Vector2D(
            (goal.groundPlane - myPos.vector + shooterOffset).mag,
            goal.z - ShooterConfig.flywheelOffset.z
        )
        //println("targetPoint2D $targetPoint2D")

        val throughPoint = Vector2D(
            targetPoint2D.x + ShooterConfig.throughPointOffsetX,
            (
                    goal.z + ShooterConfig.defaultThroughPointY
                            - ShooterConfig.flywheelOffset.z
                    )
        )
        //println("throughPoint $throughPoint")


        //println("targetPoint2D $targetPoint2D")

        val trajectory = ComputeTraj.computeTraj(throughPoint, targetPoint2D)
        var velocity = trajectory.first
        var launchAngle = trajectory.second

        var angleToGoal = atan2(
            goal.y - myPos.y - shooterOffset.y,
            goal.x - myPos.x - shooterOffset.x
        )

        //calculate the sides of the triangle baised from angle and hyp length.

        val velGroundPlane = cos(launchAngle) * velocity

        var vecX = cos(angleToGoal) * velGroundPlane
        var vecY = sin(angleToGoal) * velGroundPlane
        var vecZ = sin(launchAngle) * velocity

        var launchVec = Vector3D(vecX, vecY, vecZ)

        //adjust for the motion of the drive base
        launchVec = launchVec - Vector3D(botVel.x, botVel.y, 0)

        return launchVec
    }


    /**
     * This function calculates the horizontal velocity of a projectile
     * on a 2D plane.
     */
    fun compHorzVel(
        zVelocity: Double,
        horzTarg: Double,
        vertTarg: Double,
        vBot:Double
    ): Double{

        return (horzTarg*-386)/
                (-zVelocity-sqrt(zVelocity.pow(2)+2*(-386)*vertTarg))-vBot
    }

    fun compFlywheelDependantVec(
        to: Vector3D,
        from: Vector3D,
        botVel: Pose2D,
        curSpeed: Double
    ): Vector3D {
        //Compute the launch vec based on flywheel velocity
        val target = to - from
        //find the zVel
        var zVel = 0.0
        var xVel: Double
        var yVel: Double
        var prev: Double
        var mag: Double

        xVel = compHorzVel(zVel, target.x, target.z, botVel.x)
        yVel = compHorzVel(zVel, target.y, target.z, botVel.y)
        mag = sqrt(xVel.pow(2) + yVel.pow(2) + curSpeed.pow(2))
        prev = mag
        zVel++

        do {
            xVel = compHorzVel(zVel, target.x, target.z, botVel.x)
            yVel = compHorzVel(zVel, target.y, target.z, botVel.y)
            prev = mag
            mag = sqrt(xVel.pow(2) + yVel.pow(2) + zVel.pow(2))
            zVel++
        } while (!(
                    (curSpeed > mag
                            && curSpeed < prev)
                            || (curSpeed < mag
                            && curSpeed > prev))
            && zVel < 1000
        )
        val launchVec = Vector3D(xVel, yVel, zVel)
        return launchVec
    }
}