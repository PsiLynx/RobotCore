package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import kotlin.contracts.contract
import kotlin.math.PI

class Arc(
    val start: Vector2D,
    val tangent: Vector2D,
    val direction: Direction,
    val r: Double,
    val theta: Rotation2D,
    val heading: HeadingType,
): PathSegment(
    start,
    heading = heading
) {
    val center = (
        start
        + ( tangent.unit rotatedBy (PI/2*direction.dir) ) * r
    )
    init {
        controlPoints = arrayOf(
            start,
            ( (center - start) rotatedBy (theta*direction.dir) )
            + center
        )
    }

    override fun point(t: Double) = (
        ( (center - start) rotatedBy (theta*t*direction.dir) )
        + center
    )
    override fun velocity(t: Double) = Vector2D()
    override fun accel(t: Double) = Vector2D()

    override fun lenFromT(t: Double) = 0.0

    override fun closestT(point: Vector2D) = 0.0

    override val Cmax: Double = 1/r

    enum class Direction(val dir: Int) {
        LEFT(-1), RIGHT(1)
    }

}