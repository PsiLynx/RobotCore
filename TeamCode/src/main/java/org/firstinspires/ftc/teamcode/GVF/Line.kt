package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Vector2D
import org.firstinspires.ftc.teamcode.util.Vector2D.Companion.mag
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.PI

class Line(var p1: Vector2D, var p2: Vector2D): PathSegment() {
    constructor(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double
    ): this(Vector2D(x1, y1), Vector2D(x2, y2))

    override fun closestT(point: Vector2D): Double{
        val l1 = mag(point - p1)
        val alpha = atan2(point.y - p1.y, point.x - p1.x)-atan2(p2.y-p1.y, p2.x-p1.x)
	val beta = PI/2 - alpha
	val l2 = sin(beta) * l1 / sin(PI/2)
	val d = mag(p2 - p1)

	return l2/d
    }
}
