package org.ftc3825.gvf

import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.geometry.Vector2D

class Builder {
    private var pathSegments = arrayListOf<PathSegment>()
    private var callbacks = arrayListOf<Pair<Int, InstantCommand>>()
    private var lastPoint = Vector2D()
    private var lastTangent = Vector2D()
    private var stopAtEnd = true

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

//    fun curveTo(x1: Number, y1: Number, x2: Number, y2: Number, heading: HeadingType){
//        val segment = Spline(
//            lastPoint,
//            lastTangent,
//            Vector2D(x1, y1),
//            Vector2D(x2, y2),
//            heading
//        )
//        lastPoint = segment.end
//        lastTangent = segment.tangent(1.0)
//        pathSegments.add(segment)
//    }
    fun curveTo(x1: Number, y1: Number, x2: Number, y2: Number, x3: Number, y3: Number, heading: HeadingType){
        val segment = Spline(
            lastPoint,
            Vector2D(x1, y1),
            Vector2D(x2, y2),
            Vector2D(x3, y3),
            heading
        )
        lastPoint = segment.end
        lastTangent = segment.tangent(1.0)
        pathSegments.add(segment)
    }
    fun quickly(command: InstantCommand){
        callbacks.add( (pathSegments.size - 1) to command)
    }

    fun stop() = pathSegments[pathSegments.size - 1].stopAtEnd()
    fun build(): Path {
        val path = Path(pathSegments).apply {
            this.callbacks = this@Builder.callbacks
        }
        if(stopAtEnd) { path[-1].stopAtEnd() }
        else { path[-1].dontStop() }

        Drivetrain.gvfPaths.add(path)
        return path
    }
    fun dontStop() { stopAtEnd = false }
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
