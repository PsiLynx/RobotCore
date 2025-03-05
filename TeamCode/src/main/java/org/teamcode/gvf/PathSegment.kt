package org.teamcode.gvf

import org.teamcode.gvf.GVFConstants.PATH_END_T
import org.teamcode.gvf.HeadingType.Constant
import org.teamcode.gvf.HeadingType.Linear
import org.teamcode.gvf.HeadingType.Tangent
import org.teamcode.util.geometry.Rotation2D
import org.teamcode.util.geometry.Vector2D
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

abstract class PathSegment(private vararg var controlPoints: Vector2D, private val heading: HeadingType) {
    val end = controlPoints[controlPoints.size - 1]
    var atEnd = false
        internal set
    var fractionComplete = 0.0
        internal set
    var stopAtEnd = false

    abstract val length: Double

    fun stopAtEnd() { stopAtEnd = true }

    fun targetHeading(t: Double) = when(heading) {
        is Tangent -> tangent(t).theta
        is Constant -> heading.theta
        is Linear -> heading.theta1 * (1 - t) + heading.theta2 * t
    }

    abstract fun tangent(t: Double) : Vector2D
    abstract fun accel(t: Double) : Vector2D
    abstract fun closestT(point: Vector2D): Double
    abstract fun point(t: Double): Vector2D

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
        } else tangent(closestT).unit
    )
    fun curvature(closestT: Double): Double {
        val vel = tangent(closestT)
        val acc = accel(closestT)
        val output = vel.unit rotatedBy Rotation2D(PI / 2)
        val k = (vel.x * acc.y - vel.y * acc.x) / vel.mag.pow(3)

        return k

    }
    override fun toString() = "PathSegment: $controlPoints"
    fun dontStop() { stopAtEnd = false }
}
