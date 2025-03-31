package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.gvf.GVFConstants.PATH_END_T
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Constant
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Linear
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Tangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.ReverseTangent
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
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
        is Tangent -> tangent(t).theta
        is ReverseTangent -> tangent(t).theta + Rotation2D(PI / 2)
        is Constant -> heading.theta
        is Linear -> heading.theta1 * (1 - t) + heading.theta2 * t
    }

    abstract fun point(t: Double): Vector2D
    abstract fun accel(t: Double) : Vector2D
    abstract fun lenFromT(t: Double): Double
    abstract fun tangent(t: Double) : Vector2D
    abstract fun closestT(point: Vector2D): Double

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
}
