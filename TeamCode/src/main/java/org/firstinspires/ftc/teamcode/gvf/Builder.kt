package org.firstinspires.ftc.teamcode.gvf

import com.sun.tools.doclint.Entity.delta
import org.firstinspires.ftc.teamcode.command.RamseteCommand
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class Builder {
    var pathSegments = arrayListOf<PathSegment>()
    var lastPoint = Vector2D()
    var lastTangent = Vector2D()
    var lastEndVel = 0.1
    var setEndVel = false

    fun start(x: Number, y: Number) { lastPoint = Vector2D(x.toDouble(), y.toDouble()) }
    fun start(point: Vector2D) = start(point.x, point.y)

    fun lineTo(x: Number, y: Number, heading: HeadingType){
        val segment = Line(
            lastPoint,
            Vector2D(x, y),
            lastEndVel,
            1.0,
            heading,
        )
        lastPoint = segment.end
        lastTangent = segment.velocity(1.0)
        lastEndVel = 1.0
        pathSegments.add(segment)
        setEndVel = false
    }
    fun lineTo(point: Vector2D, heading: HeadingType) =
        lineTo(point.x, point.y, heading)

    fun straight(distance: Number, heading: HeadingType) =
        lineTo(lastPoint + lastTangent.unit * distance, heading)

    fun arc(
        direction: Arc.Direction,
        theta: Number,
        r: Number,
        heading: HeadingType
    ){
        val segment = Arc(
            lastPoint,
            lastTangent,
            direction,
            r.toDouble(),
            Rotation2D(theta),
            lastEndVel,
            1.0,
            heading
        )
        lastPoint = segment.point(1.0)
        lastTangent = segment.velocity(1.0)
        lastEndVel = 1.0
        pathSegments.add(segment)
        setEndVel = false

    }
    fun arcLineTo(direction: Arc.Direction, x: Number, y: Number, r: Number, heading: HeadingType){
        val relativeTarget = (
            ( Vector2D(x, y) - lastPoint )
            rotatedBy ( Rotation2D(PI/2) - lastTangent.theta )
        )
        val alpha = atan2(
            relativeTarget.y,
            r.toDouble() - relativeTarget.x
        )
        val delta = acos(
            r.toDouble() / sqrt(
                (r.toDouble() - relativeTarget.x).pow(2)
                        + relativeTarget.y.pow(2)
            )
        ) * direction.dir
        arc(
            direction,
            minOf(abs(alpha + delta), abs(alpha - delta)),
            r,
            heading
        )
        lineTo(x, y, heading)
    }

    fun arcLeft(theta: Number, r: Number, heading: HeadingType) = arc(
        Arc.Direction.LEFT, theta, r, heading
    )

    fun arcRight(theta: Number, r: Number, heading: HeadingType) = arc(
        Arc.Direction.RIGHT, theta, r, heading
    )

    fun curveTo(cx1: Number, cy1: Number, cx2: Number, cy2: Number, x2: Number, y2: Number, heading: HeadingType){
        val segment = Spline(
            lastPoint,
            Vector2D(cx1, cy1),
            Vector2D(cx2, cy2),
            Vector2D(x2, y2),
            lastEndVel,
            1.0,
            heading
        )
        lastPoint = segment.end
        lastTangent = segment.velocity(1.0)
        lastEndVel = 1.0
        pathSegments.add(segment)
        setEndVel = false
    }
    fun endVel(vel: Double) {
        pathSegments.last().v_f = vel
        lastEndVel = vel
        setEndVel = true
    }

    fun build(): Path {
        val path = Path(pathSegments)
        if(!setEndVel) {
            path[-1].v_f = 0.0
        }

        return path
    }
}
fun path(builder: Builder.() -> Unit) = Builder().apply(builder).build()
fun followPath(builder: Builder.() -> Unit) =
    RamseteCommand(path(builder))

sealed interface HeadingType {
    data class Constant(val theta: Rotation2D): HeadingType
    data class Linear(val theta1: Rotation2D, val theta2: Rotation2D): HeadingType
    data class RelativeToTangent(val offset: Rotation2D): HeadingType
    class Tangent: HeadingType
    class ReverseTangent: HeadingType
    companion object{
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

        val tangent = Tangent()
        val reverseTangent = ReverseTangent()

        val forward = Constant(Rotation2D(PI / 2    ))
        val left    = Constant(Rotation2D(PI        ))
        val right   = Constant(Rotation2D(0         ))
        val back    = Constant(Rotation2D(3 * PI / 2))
    }
}
