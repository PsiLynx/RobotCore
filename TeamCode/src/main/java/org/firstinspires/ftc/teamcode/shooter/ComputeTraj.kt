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
import org.firstinspires.ftc.teamcode.util.log
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
        correctDecimals: Double = 0.001,
        maxNumItterations: Int = 20
    ): Result<Vector3D> {
        //Compute the launch vec based on flywheel velocity
        val target = to - from

        val targetPos: (Double) -> Vector3D = { t -> target - botVel.vector.toVector3D() * t }
        val targetVel = botVel.vector.toVector3D()

        /** Computes the required magnitude for a specific time to target. */
        val magnitude: (Double) -> Double = { t ->
            val pos = targetPos(t)
            sqrt(
                (pos.x / t).pow(2.0) +
                        (pos.y / t).pow(2.0) +
                        (pos.z / t - 0.5 * g * t).pow(2.0)
            )
        }

        /** Correct derivative of magnitude for Newton's method. */
        val magPrime: (Double) -> Double = { t ->
            val pos = targetPos(t)
            val vel = targetVel // derivative of targetPos(t)

            val X = pos.x / t
            val Y = pos.y / t
            val Z = pos.z / t - 0.5 * g * t

            val Xprime = (vel.x * t - pos.x) / (t * t)
            val Yprime = (vel.y * t - pos.y) / (t * t)
            val Zprime = (vel.z * t - pos.z) / (t * t) - 0.5 * g

            val numerator = X * Xprime + Y * Yprime + Z * Zprime
            val denominator = sqrt(X * X + Y * Y + Z * Z)

            numerator / denominator
        }

        /**
         * This function is an optimization of the time to mag
         * function and its directive.
         * @param t time to target
         * @param curSpeed the set shot magnitude
         * @param botVel The velocity of the robot
         * @param g I hope you know what this is...
         * @return Something that I can shove
         * into neutons method and it apparently works.
         */

        fun magOverMagPrime(
            t: Double,
            curSpeed: Double,
            botVel: Vector3D,
            g: Double
        ): Double {
            val target = targetPos(t)
            val tx = target.x
            val ty = target.y
            val tz = target.z

            // precompute powers of t
            val t2 = t * t
            val t3 = t2 * t
            val t4 = t3 * t
            val halfGT = 0.5 * g * t

            // precompute reusable components
            val txOverT = tx / t
            val tyOverT = ty / t
            val tzOverTMinusHalfGT = tz / t - halfGT

            // magnitude
            val mag = sqrt(txOverT * txOverT + tyOverT * tyOverT + tzOverTMinusHalfGT * tzOverTMinusHalfGT)
            val numerator = mag - curSpeed

            // denominator
            val dx = botVel.x - txOverT
            val dy = botVel.y - tyOverT
            val dz = tzOverTMinusHalfGT
            val denomSqrt = sqrt(dx * dx + dy * dy + dz * dz)

            // numerator of magPrime
            val denomNumerator = 0.25 * g * g * t4 + t * tx * botVel.x + t * ty * botVel.y - 2.0 * tx * tx - ty * ty
            val denominator = denomNumerator / (t3 * denomSqrt)

            return numerator / denominator
        }
//magOverMagPrime(targetTime, curSpeed, botVel.vector.toVector3D(), g)
        //neutons method:
        var targetTime = impactTimeGuess
        var xPrev = targetTime
        var numItterations = 0
        do{
            xPrev = targetTime
            targetTime = xPrev - (magnitude(targetTime)-curSpeed)/magPrime(targetTime)

            if(numItterations > maxNumItterations || targetTime < 0) {
                log("Num itterations before exiting") value numItterations
                log("shotTime") value targetTime
                return Result.failure(
                    TimeoutException(
                        "Max Number of iterations of neutons method reached."
                    )
                )
            }
            numItterations ++
        }
        while(abs(targetTime - xPrev) > correctDecimals)

        log("Num itterations before exiting") value numItterations
        log("shotTime") value targetTime
        //now I have the targettime. Compute the velocities necessary.
        val xVel = targetPos(targetTime).x / targetTime
        val yVel = targetPos(targetTime).y / targetTime
        val zVel = targetPos(targetTime).z / targetTime - 0.5*g*targetTime
        val launchVec = Vector3D(xVel, yVel, zVel)
        return Result.success(launchVec)
    }
}