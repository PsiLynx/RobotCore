package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.gvf.GVFConstants.PATH_END_T
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Constant
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Linear
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Tangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.ReverseTangent
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

abstract class PathSegment(private vararg var controlPoints: Vector2D, private val heading: HeadingType) {
    val end = controlPoints[controlPoints.size - 1]
    var atEnd = false
        internal set
    var fractionComplete = 0.0
        internal set
    var endVelocity = 1.0

    fun targetHeading(t: Double) = when(heading) {
        is Tangent -> velocity(t).theta
        is ReverseTangent -> velocity(t).theta + Rotation2D(PI / 2)
        is Constant -> heading.theta
        is Linear -> heading.theta1 * (1 - t) + heading.theta2 * t
    }

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

    fun reset(){ atEnd = false }

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
            atEnd = true
            Vector2D()
        } else velocity(closestT).unit
    )
    fun curvature(closestT: Double): Double {
        val vel = velocity(closestT)
        val acc = accel(closestT)
        val k = (vel.x * acc.y - vel.y * acc.x) / abs(vel.mag.pow(3))

        return k

    }
    override fun toString() = "PathSegment: $controlPoints"
}
