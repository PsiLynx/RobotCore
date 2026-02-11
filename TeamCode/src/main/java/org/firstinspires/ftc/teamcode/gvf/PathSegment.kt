package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.controller.mp.TrapMpParams
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
    abstract var v_0: Double
    abstract var v_f: Double

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
     * where s is position along the curve (in/s)
     */
    fun targetHeadingDerivative(t: Double) = Rotation2D(
        when(heading) {
            is Tangent -> curvature(t)
            is ReverseTangent -> -curvature(t)
            is Constant -> 0.0
            is Linear -> (heading.theta1 - heading.theta2).toDouble() / length
            is RelativeToTangent -> curvature(t)
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
    open fun curvature(closestT: Double): Double {
        val vel = velocity(closestT)
        val acc = accel(closestT)
        val k = (vel.x * acc.y - vel.y * acc.x) / abs(vel.mag.pow(3))

        return k

    }
    override fun toString() = "PathSegment: $controlPoints"
}
