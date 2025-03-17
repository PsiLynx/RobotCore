package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D

class Builder {
    private var pathSegments = arrayListOf<PathSegment>()
    private var callbacks = arrayListOf<Pair<Int, InstantCommand>>()
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
        lastTangent = segment.tangent(1.0)
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
        lastTangent = segment.tangent(1.0)
        pathSegments.add(segment)
    }
    fun quickly(command: InstantCommand){
        callbacks.add( (pathSegments.size - 1) to command)
    }

    fun build(): Path {
        val path = Path(pathSegments).apply {
            this.callbacks = this@Builder.callbacks
            this[-1].endVelocity = 0.0
        }

        Drivetrain.gvfPaths.add(path)
        return path
    }
}
fun path(builder: Builder.() -> Unit) = Builder().apply(builder).build()
fun followPath(builder: Builder.() -> Unit) =
    FollowPathCommand(path(builder))

sealed interface HeadingType {
    data class Constant(val theta: Rotation2D): HeadingType
    data class Linear(val theta1: Rotation2D, val theta2: Rotation2D): HeadingType
    class Tangent: HeadingType
    companion object{
        fun tangent() = Tangent()
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
    }
}
