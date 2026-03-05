package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.geometry.Vector2D

class Line(
    var p1: Vector2D,
    var p2: Vector2D,
    override var v_0: Double = 1.0,
    override var v_f: Double = 1.0,
    heading: HeadingType
): PathSegment(
    p1,
    p2,
    heading = heading
) {
    constructor(
        x1: Number,
        y1: Number,
        x2: Number,
        y2: Number,
        v_0: Double,
        v_f: Double,
        heading: HeadingType
    ): this(
        Vector2D(x1, y1),
        Vector2D(x2, y2),
        v_0,
        v_f,
        heading
    )

    override val Cmax = 0.0

    override fun closestT(point: Vector2D): Double{
        val u = p2 - p1
        val v = point - p1

        return ( (u dot v) / u.magSq ).coerceIn(0.0, 1.0)
    }

    override fun point(t: Double) = p1 * (1 - t) + p2 * t
    override fun velocity(t: Double) = ( p2 - p1 )
    override fun accel(t: Double) = Vector2D()
    override fun jerk(t: Double) = Vector2D()

    override fun lenFromT(t: Double) = ( 1 - t ) * ( p2 - p1 ).mag

    override fun toString() = "Line: $p1, $p2"
}
