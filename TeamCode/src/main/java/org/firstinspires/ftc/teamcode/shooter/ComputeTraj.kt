package org.firstinspires.ftc.teamcode.shooter

import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.subsystem.Hood
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
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
        println("launchAngle $launchAngle")
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
}