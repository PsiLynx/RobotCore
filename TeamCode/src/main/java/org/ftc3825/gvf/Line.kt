package org.ftc3825.gvf

import org.ftc3825.util.geometry.Vector2D

class Line(
    var p1: Vector2D,
    var p2: Vector2D,
    heading: HeadingType
): PathSegment(p1, p2, heading = heading) {
    constructor(
        x1: Number,
        y1: Number,
        x2: Number,
        y2: Number,
        heading: HeadingType
    ): this(Vector2D(x1, y1), Vector2D(x2, y2), heading)

    override fun closestT(point: Vector2D): Double{
        val u = p2 - p1
        val v = point - p1

        return ( (u dot v) / u.magSq ).coerceIn(0.0, 1.0)
    }

    override val length = ( p2 - p1 ).mag


    override fun tangent(t: Double) = ( p2 - p1 ).unit
    override fun accel(t: Double) = Vector2D()
    override fun point(t: Double) = p1 * (1 - t) + p2 * t

    override fun toString() = "Line: ($p1), ($p2)"
}
