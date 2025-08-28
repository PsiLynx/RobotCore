package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.util.centimeters
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class Circle(
    val center: Vector2D,
    val r: Double,
    headingType: HeadingType
): PathSegment(center, center + Vector2D(1, 0) * r, heading = headingType) {
    override fun point(t: Double) = (
        Vector2D(1, 0)
            rotatedBy Rotation2D( t * 2 * PI )
    ) * r + center

    override fun accel(t: Double): Vector2D {
        return (point(t) - center) * ( - ( 2 * PI ).pow(2) )
    }

    override fun lenFromT(t: Double): Double {
        return ( 1 - t ) * 2 * PI * r
    }

    override fun velocity(t: Double): Vector2D {
        return ( point(t) - center ) * ( 2 * PI ) rotatedBy Rotation2D( PI / 2 )
    }

    override fun closestT(point: Vector2D): Double {
        val v = point - center
        val theta = v.theta
        val normalized = (theta  / (2 * PI)).toDouble() % 1.0
        return if (normalized < 0) normalized + 1.0 else normalized
    }
}