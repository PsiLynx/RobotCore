package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandGroup
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.geometry.Vector2D

class MultipleBuilder {
    var paths = arrayListOf<Triple<Path, Double, Double>>()
    var pathSegments = arrayListOf<PathSegment>()
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
    fun stop(posConstraint: Double = 2.0, velConstraint: Double = 5.0){
        paths.add( Triple(build(), posConstraint, velConstraint) )
    }

    fun build(): Path {
        val path = Path(pathSegments).apply {
            this[-1].endVelocity = 0.0
        }

        Drivetrain.gvfPaths.add(path)
        pathSegments = arrayListOf()
        return path
    }
}

fun followPaths(builder: MultipleBuilder.() -> Unit): Command {
    val commands = arrayListOf<FollowPathCommand>()
    val obj = MultipleBuilder().apply {
        apply(builder)
        stop()
        paths.forEach {
            commands.add(
                FollowPathCommand(it.first)
                    .withConstraints(it.second, it.third)
            )
        }
    }
    return CommandGroup(*commands.toTypedArray())
}
