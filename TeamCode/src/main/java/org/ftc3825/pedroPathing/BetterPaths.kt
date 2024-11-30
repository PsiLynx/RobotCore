package org.ftc3825.pedroPathing

import org.ftc3825.command.FollowPedroPath
import org.ftc3825.pedroPathing.pathGeneration.PathBuilder
import org.ftc3825.pedroPathing.pathGeneration.PathChain
import org.ftc3825.pedroPathing.pathGeneration.Point
import org.ftc3825.subsystem.Drivetrain

class Builder() {
    var pathBuilder = PathBuilder()
    private var lastPoint = Point(0.0, 0.0)

    private fun setHeading(heading: HeadingType) {
        when (heading) {
            is Tangent -> pathBuilder.setTangentHeadingInterpolation()
            is Constant -> pathBuilder.setConstantHeadingInterpolation(heading.theta)
            is Linear -> pathBuilder.setLinearHeadingInterpolation(heading.theta1, heading.theta2)
        }
    }
    fun start(x: Number, y: Number) { lastPoint = Point(x.toDouble(), y.toDouble()) }
    fun lineTo(x: Number, y: Number, heading: HeadingType){
        val end = Point(x.toDouble(), y.toDouble())
        pathBuilder.addBezierLine(lastPoint, end)
        lastPoint = end
        setHeading(heading)
    }
    fun curveTo(x1: Number, y1: Number, x2: Number, y2: Number, heading: HeadingType){
        val end = Point(x2.toDouble(), y2.toDouble())
        pathBuilder.addBezierCurve(
            lastPoint,
            Point(x1.toDouble(), y1.toDouble()),
            end
        )
        lastPoint = end
        setHeading(heading)
    }
    fun curveTo(x1: Number, y1: Number, x2: Number, y2: Number, x3: Number, y3: Number, heading: HeadingType){
        val end = Point(x3.toDouble(), y3.toDouble())
        pathBuilder.addBezierCurve(
            lastPoint,
            Point(x1.toDouble(), y1.toDouble()),
            Point(x2.toDouble(), y2.toDouble()),
            end
        )
        lastPoint = end
        setHeading(heading)
    }

    fun build(): PathChain {
        val path = pathBuilder.build()
        Drivetrain.allPaths.add(path)
        return path
    }
}
fun path(builder: Builder.() -> Unit) = Builder().apply(builder).build()
fun followPath(builder: Builder.() -> Unit) = FollowPedroPath(path(builder))

interface HeadingType {
    companion object{
        fun tangent() = Tangent()
        fun constant(theta: Double) = Constant(theta)
        fun linear(theta1: Double, theta2: Double) = Linear(theta1, theta2)
    }
}
data class Constant(val theta: Double): HeadingType
data class Linear(val theta1: Double, val theta2: Double): HeadingType
class Tangent: HeadingType
