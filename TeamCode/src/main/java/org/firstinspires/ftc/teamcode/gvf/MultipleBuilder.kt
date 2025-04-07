package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandGroup
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D

class MultipleBuilder {
    var paths = arrayListOf<Path>()
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
    fun endVel(vel: Double) { pathSegments.last().endVelocity = vel }
    fun stop(){
        paths.add(build())
        pathSegments = arrayListOf()
    }

    fun build(): Path {
        val path = Path(pathSegments).apply {
            this[-1].endVelocity = 0.0
        }

        Drivetrain.gvfPaths.add(path)
        return path
    }
}

fun followPaths(builder: MultipleBuilder.() -> Unit): Command {
    val commands = arrayListOf<FollowPathCommand>()
    val obj = MultipleBuilder()
    obj.apply(builder)
    obj.stop()
    obj.paths.forEach { commands.add(FollowPathCommand(it)) }
    return CommandGroup(*commands.toTypedArray())
}
