package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.tan

class Arc(
    val start: Vector2D,
    val tangent: Vector2D,
    val direction: Direction,
    val r: Double,
    val theta: Rotation2D,
    val heading: HeadingType,
): PathSegment(
    start,
    start
        + ( tangent.unit rotatedBy (PI/2*direction.dir) )
        - tangent.unit rotatedBy (
            PI/2*direction.dir + theta.toDouble() *direction.dir
        ),
    heading = heading
) {
    val center = (
        start
        + ( tangent.unit rotatedBy (PI/2*direction.dir) ) * r
    )

    val theta_0 = ( start - center ).theta
    val theta_f = theta_0 + theta * direction.dir
    val d_theta_dt = (theta_f - theta_0)
    init {
        controlPoints = arrayOf(
            start,
            point(1.0)
        )
    }

    override fun point(t: Double) = (
        ( (start - center) rotatedBy (theta*t*direction.dir) )
        + center
    )
    override fun velocity(t: Double) = (
        ( tangent.unit rotatedBy ( d_theta_dt * t * direction.dir ) )
         * abs(d_theta_dt.toDouble())*r
    )
    override fun accel(t: Double) =
        ( center - point(t) ).unit * d_theta_dt.toDouble().pow(2) * r

    override fun lenFromT(t: Double) = d_theta_dt.toDouble() * r * (1 - t)

    override fun closestT(point: Vector2D) = (
        ( (point - center).theta - theta_0 ).toDouble()
        / d_theta_dt.toDouble()
    )

    override val Cmax: Double = 1/r

    enum class Direction(val dir: Int) {
        LEFT(1), RIGHT(-1)
    }

}