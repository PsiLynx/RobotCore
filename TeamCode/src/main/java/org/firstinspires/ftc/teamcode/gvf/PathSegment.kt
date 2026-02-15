package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.gvf.GVFConstants.PATH_END_T
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Constant
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Linear
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Tangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.ReverseTangent
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType.RelativeToTangent
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

abstract class PathSegment(
    protected vararg var controlPoints: Vector2D,
    val heading: HeadingType,
) {
    open var v_0 = 1.0
    open var v_f = 1.0

    val end get() = point(1.0)
    val length get() = lenFromT(0.0)

    fun atEnd(closestT: Double) = closestT > PATH_END_T

    var fractionComplete = 0.0
        internal set

    fun targetHeading(t: Double) = when(heading) {
        is Tangent -> velocity(t).theta
        is ReverseTangent -> (velocity(t).theta + Rotation2D(PI)).wrap()
        is Constant -> heading.theta
        is Linear -> heading.theta1 * (1 - t) + heading.theta2 * t
        is RelativeToTangent -> velocity(t).theta + heading.offset
    }

    /**
     * @return d_theta / d_s
     * where s is position along the curve (in)
     */
    fun targetHeadingVelocity(t: Double) = Rotation2D(
        when(heading) {
            is Tangent -> curvature(t)
            is ReverseTangent -> -curvature(t)
            is Constant -> 0.0
            is Linear -> (heading.theta1 - heading.theta2).toDouble() / length
            is RelativeToTangent -> curvature(t)
        }
    )

    /**
     * @return d^2_theta / d_s^2
     * where s is position along the curve (in)
     * i.e the second derivative of target Heading.
     * Uses the heading type
     */
    fun targetHeadingAccel(t: Double) = Rotation2D(
        when(heading) {
            is Tangent -> headingAcceleration(t)
            is ReverseTangent -> -headingAcceleration(t)
            is Constant -> 0.0
            is Linear -> 0.0
            is RelativeToTangent -> headingAcceleration(t)
        }
    )

    abstract fun point(t: Double): Vector2D

    /**
     * @param t the position along the curve, [0, 1]
     * @return the vector along the curve, NOT normalized
     */
    abstract fun velocity(t: Double) : Vector2D

    /**
     * @param t the position along the curve, [0, 1]
     * @return the acceleration vector, NOT normalized
     */
    abstract fun accel(t: Double) : Vector2D

    /**
     * @param t the position along the curve, [0, 1]
     * @return the jerk vector, NOT normalized
     */
    abstract fun jerk(t: Double) : Vector2D

    abstract fun lenFromT(t: Double): Double
    abstract fun closestT(point: Vector2D): Double

    /**
     * max curvature on the range 0 < t < 1
     */
    abstract val Cmax: Double

    fun distToEnd(currentPos: Vector2D) = (
        //( currentPos - point(closestT(currentPos)) ).mag +
        lenFromT(closestT(currentPos))
    )

    fun getRotationalError(currentHeading: Rotation2D, t: Double) = Rotation2D(
        arrayListOf(
            (targetHeading(t) - currentHeading).toDouble(),
            (targetHeading(t) - currentHeading).toDouble() + 2*PI,
            (targetHeading(t) - currentHeading).toDouble() - 2*PI,
        ).minBy { abs(it) }
    )
    fun getNormalVector(currentPos: Vector2D, closestT: Double) = (
        point(closestT) - currentPos
    )
    fun getTangentVector(currentPos: Vector2D, closestT: Double) = (
        if ( closestT > PATH_END_T ) {
            Vector2D()
        } else velocity(closestT).unit
    )

    /**
     * [wikipedia on curvature](https://en.wikipedia.org/w/index.php?title=Curvature&oldid=1251369919#In_terms_of_a_general_parametrization)
     */
    open fun curvature(closestT: Double): Double {
        val vel = velocity(closestT)
        val acc = accel(closestT)
        val k = (vel.x * acc.y - vel.y * acc.x) / vel.mag.pow(3)

        return k

    }

    /**
     * d/dt of curvature
     */
    open fun headingAcceleration(closestT: Double): Double {
        val vel  = velocity(closestT)
        val acc  = accel(closestT)
        val jerk = jerk(closestT)

        return (
            vel.mag.pow(3) * (
                + vel.x * jerk.y
                - vel.y * jerk.x
            ) // low d high
            - (
                (vel.x * acc.y - vel.y * acc.x)
                * ( 3 * vel.mag * (vel dot acc) )
            ) // high d low
        ) / vel.mag.pow(7)
    }
    open fun tFromDist(dist: Double) = dist / length

    override fun toString() = "PathSegment: $controlPoints"
}
