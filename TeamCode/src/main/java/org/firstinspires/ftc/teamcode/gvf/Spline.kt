package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.gvf.GVFConstants.SPLINE_RES
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import kotlin.math.ceil

class Spline(
    val p1: Vector2D,
    val cp1: Vector2D,
    val cp2: Vector2D,
    val p2: Vector2D,
    heading: HeadingType
): PathSegment(p1, cp1, cp2, p2, heading = heading) {
    constructor(
        x1:Number, y1:Number,
        cx1:Number, cy1:Number,
        x2:Number, y2:Number,
        cx2:Number, cy2:Number,
        heading: HeadingType
    ) : this(
        Vector2D(x1, y1),
        Vector2D(cx1, cy1),
        Vector2D(cx2, cy2),
        Vector2D(x2, y2),
        heading = heading
    )
    val pointsInLUT = ceil(1 / SPLINE_RES).toInt()

    private val v1 = p1 + cp1
    private val v2 = p2 - cp2

    private val coef = Array(4) {
        when (it) {
            0 ->  p1
            1 -> -p1*3.0   + v1*3.0
            2 ->  p1*3.0   - v1*6.0 + v2*3.0
            3 -> -p1       + v1*3.0 - v2*3.0 + p2
            else -> Vector2D()
        }
    }
    private val pointsLUT = (
        Array(pointsInLUT.toInt() + 1) { t -> point(t / pointsInLUT.toDouble()) }
    )

    override val Cmax = (0..pointsInLUT).maxOf {
        curvature(it / pointsInLUT.toDouble())
    }

    override fun lenFromT(t: Double): Double {
        val points = (
            arrayListOf(*pointsLUT)
            .withIndex()
            .filter { it.index * SPLINE_RES > t }
            .map { it.value }
        )
        return (0 until points.size - 2).sumOf { i ->
            (points[i + 1] - points[i]).mag
        }
    }

    override fun closestT(point: Vector2D) = (
        pointsLUT.indexOf(
            pointsLUT.minBy { (it - point).magSq }
        ) / pointsInLUT.toDouble()
        ).coerceIn(0.0, 1.0)
    override fun point(t: Double) = (
          coef[0]
        + coef[1] * ( t )
        + coef[2] * ( t * t )
        + coef[3] * ( t * t * t )
    )
    override fun velocity(t: Double) = (
          coef[1]
        + coef[2] * ( 2 * t )
        + coef[3] * ( 3 * t * t )
    )

    override fun accel(t: Double) = (
          coef[2] * ( 2 )
        + coef[3] * ( 6 * t )
    )

    override fun toString() = "Spline: ($p1), ($p2)"
}
