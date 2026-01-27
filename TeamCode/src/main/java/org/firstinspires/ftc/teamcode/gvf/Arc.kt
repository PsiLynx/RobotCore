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
    override var v_0: Double = 1.0,
    override var v_f: Double = 1.0,
    heading: HeadingType,
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
        ( tangent.unit rotatedBy ( theta * t * direction.dir ) )
         * theta.toDouble() * r
    )
    override fun accel(t: Double) =
        ( center - point(t) ).unit * theta.toDouble().pow(2) * r

    override fun lenFromT(t: Double) = theta.toDouble() * r * (1 - t)

    override fun closestT(point: Vector2D) = (
        ( (point - center).theta - theta_0 ).toDouble() * direction.dir
        / theta.toDouble()
    ).coerceIn(0.0, 1.0)

    override val Cmax: Double = 1/r

    enum class Direction(val dir: Int) {
        LEFT(1), RIGHT(-1)
    }

}