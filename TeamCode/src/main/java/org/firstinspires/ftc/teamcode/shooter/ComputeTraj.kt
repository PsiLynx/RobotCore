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
import org.firstinspires.ftc.teamcode.shooter.ShooterConfig.g
import java.math.RoundingMode
import java.util.concurrent.TimeoutException
import kotlin.math.abs

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
        gravity: Double = g
    ): Double {
        return groundTravel * tan(launchAngle) - (-gravity * groundTravel.pow(2)) /
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
        gravity: Double = g
    ): Double {
        return -2 * groundTravel.pow(2) * cos(launchAngle).pow(2) * tan(launchAngle) + 2 * -gravity * groundTravel
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
        gravity: Double = g
    ): Double {
        return sqrt(
            -(
                    -gravity * (targetPoint - fromPos).x.pow(2))
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

        return (horzTarg*g)/
                (-zVelocity-sqrt(zVelocity.pow(2)+2*(g)*vertTarg))-vBot
    }

    /**
     * This function computes a launch vec that has a specified
     * magnitude, that will result in the projectile going through
     * the target.
     * @param to Target location
     * @param from current location
     * @param botVel the current velocity of the shooting base
     * @param curSpeed the magnitude of the launch vec
     * @param impactTimeGuess the guess for neutons method.
     * @param correctDecimals the number of decimals that must
     *                        not change from two iterations of
     *                        neutons method in order to be solved
     */
    fun hoodCompensation(
        to: Vector3D,
        from: Vector3D,
        botVel: Pose2D,
        curSpeed: Double,
        impactTimeGuess: Double,
        correctDecimals: Int = 3,
        maxNumItterations: Int = 20
    ): Result<Vector3D> {
        //Compute the launch vec based on flywheel velocity
        val target = to - from

        val targetPos: (Double) -> Vector3D = { t -> target - botVel.vector.toVector3D() * t }

        /**this function computes the required magnitude for
         * a specific time to target.
         * @param t time to target.
         * @return the magnitude of the vector required.
         */
        val magnitude:  (Double) -> Double = { t ->
            sqrt(
                (targetPos(t).x / t).pow(2.0) +
                        (targetPos(t).y / t).pow(2.0) +
                        (targetPos(t).z / t - 0.5 * g * t).pow(2.0)
            )
        }

        /**
         * This is the directive of the magnitude function for neutons method
         * GO WOLFRAMALPHA!
         * @param t time to target.
         * @return I don't want to figure out what it means right now...
         */
        val magPrime: (Double) -> Double = { t ->
            (0.25 * g.pow(2.0) * t.pow(4.0) + t * targetPos(t).x * botVel.x + t * targetPos(t).y * botVel.y - 2 * targetPos(t).x.pow(2.0) - targetPos(t).y.pow(2.0)) /
                    (t.pow(3.0) * sqrt(
                        (targetPos(t).x / t - 0.5 * g * t).pow(2.0) +
                                (botVel.x - targetPos(t).x / t).pow(2.0) +
                                (botVel.y - targetPos(t).y / t).pow(2.0)
                    ))
        }

        //neutons method:
        var targetTime = impactTimeGuess
        var xPrev = targetTime
        var numItterations = 0
        //continue until 4 decimals are correct:
        do{
            xPrev = targetTime
            targetTime = xPrev - (magnitude(xPrev)-curSpeed)/magPrime(xPrev)

            if(numItterations > maxNumItterations) return Result.failure(
                TimeoutException(
                    "Max Number of itterations of neutons method reached."
                )
            )
            numItterations ++
        }
        while(abs(targetTime - xPrev) < correctDecimals)

        //now I have the targettime. Compute the velocities necessary.
        val xVel = targetPos(targetTime).x / targetTime
        val yVel = targetPos(targetTime).y / targetTime
        val zVel = targetPos(targetTime).z / targetTime - 0.5*g*targetTime
        val launchVec = Vector3D(xVel, yVel, zVel)
        return Result.success(launchVec)
    }
}