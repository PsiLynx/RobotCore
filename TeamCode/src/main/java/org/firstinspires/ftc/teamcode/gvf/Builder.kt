package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import kotlin.math.PI

class Builder {
    private var pathSegments = arrayListOf<PathSegment>()
    private var lastPoint = Vector2D()
    private var lastTangent = Vector2D()

    fun start(x: Number, y: Number) { lastPoint = Vector2D(x.toDouble(), y.toDouble()) }
    fun start(point: Vector2D) = start(point.x, point.y)

    fun lineTo(x: Number, y: Number, heading: HeadingType){
        val segment = Line(
            lastPoint,
            Vector2D(x, y),
            heading
        )
        lastPoint = segment.end
        lastTangent = segment.velocity(1.0)
        pathSegments.add(segment)
    }
    fun lineTo(point: Vector2D, heading: HeadingType)
        = lineTo(point.x, point.y, heading)

    fun curveTo(cx1: Number, cy1: Number, cx2: Number, cy2: Number, x2: Number, y2: Number, heading: HeadingType){
        val segment = Spline(
            lastPoint,
            Vector2D(cx1, cy1),
            Vector2D(cx2, cy2),
            Vector2D(x2, y2),
            heading
        )
        lastPoint = segment.end
        lastTangent = segment.velocity(1.0)
        pathSegments.add(segment)
    }
    fun endVel(vel: Double) { pathSegments.last().endVelocity = vel }

    fun build(): Path {
        val path = Path(pathSegments).apply {
            this[-1].endVelocity = 0.0
        }

        return path
    }
}
fun path(builder: Builder.() -> Unit) = Builder().apply(builder).build()
fun followPath(builder: Builder.() -> Unit) =
    FollowPathCommand(path(builder))

sealed interface HeadingType {
    data class Constant(val theta: Rotation2D): HeadingType
    data class Linear(val theta1: Rotation2D, val theta2: Rotation2D): HeadingType
    data class RelativeToTangent(val offset: Rotation2D): HeadingType
    class Tangent: HeadingType
    class ReverseTangent: HeadingType
    companion object{
        fun tangent() = Tangent()
        fun reverseTangent() = ReverseTangent()
        fun constant(theta: Double) = Constant(Rotation2D(theta))
        fun constant(theta: Rotation2D) = Constant(theta)
        fun linear(theta1: Rotation2D, theta2: Rotation2D) = Linear(
            theta1,
            theta2
        )
        fun linear(theta1: Double, theta2: Double) = Linear(
            Rotation2D(theta1),
            Rotation2D(theta2)
        )

        val forward = Constant(Rotation2D(PI / 2    ))
        val left    = Constant(Rotation2D(PI        ))
        val right   = Constant(Rotation2D(0         ))
        val back    = Constant(Rotation2D(3 * PI / 2))
    }
}
