package org.firstinspires.ftc.teamcode.trajcode

import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.Globals

import org.firstinspires.ftc.teamcode.util.Globals.robotWidth
import org.firstinspires.ftc.teamcode.util.Globals.goalPoseCenter
import org.firstinspires.ftc.teamcode.util.Globals.throughPointCenter

/**
 * This object is responsible for calculating the goal position
 */
object ComputeGoalThings {

    val flipflop =
        if (Globals.alliance == Globals.Alliance.RED) 1
        else -1
    val verticalThroughPoint: Vector2D
        get() {
            return throughPointCenter
        }

    val horizontalThroughPoint: Vector2D
        get(){
            return goalPoseCenter.groundPlane-
                    Vector2D(robotWidth*flipflop,robotWidth)*0.5
        }

    /**
     * This function will calculate the dynamic goal position baised
     * off of the position provided and the horizontal through point
     * @param myPos The current position of the robot.
     */
    fun goalPos(myPos: Vector2D = TankDrivetrain.position.vector): Vector3D
        {
            //check for intersect on sidewall:
            val goalBack = LinearFunction(0.0, goalPoseCenter.y)

            val goalSide = LinearFunction(null, goalPoseCenter.x)

            val targetLine = LinearFunction(
                horizontalThroughPoint,
                myPos)
            //println(targetLine)

            var intctGoalBack =
                goalBack.intersect(targetLine)

            var intctGoalSide =
                goalSide.intersect(targetLine)

            /**
             * Which ever x-intercept is closest to 0, use that one.
             * Otherwise, return the goalPoseCenter
             */
            if (intctGoalBack.x*flipflop < intctGoalSide.x*flipflop) {
                return Vector3D(intctGoalBack.x, intctGoalBack.y, goalPoseCenter.z)
            }
            if (intctGoalBack.x*flipflop > intctGoalSide.x*flipflop) {
                return Vector3D(intctGoalSide.x, intctGoalSide.y, goalPoseCenter.z)
            }
            else{
                return goalPoseCenter
            }
        }
}


/**
 * This class is used to represent a linear function.
 * @property m The slope of the line.
 * @property b The y-intercept of the line.
 */
class LinearFunction(val m: Double?, val b: Double){

    /**
     * Creates a vertical line
     * @param x The x-intercept of the line.
     * Example: LinearFunction(null, x-int)
     */
    constructor(x: Double) : this(m=null, b=x)

    /**
     * Creates a line from two points.
     * @param p1 The first point.
     * @param p2 The second point.
     */
    constructor(p1: Vector2D, p2: Vector2D) : this(
        m = if (p1.x == p2.x) null else (p1.y - p2.y) / (p1.x - p2.x),

        b = if (p1.x == p2.x) p1.x else p1.y - ((p1.y - p2.y) / (p1.x - p2.x)) * p1.x
    )

    /**
     * Computes the intersect of another LinearFunction with this one.
     * @param other The other LinearFunction.
     * @return The point of intersection.
     */
    fun intersect(other: LinearFunction): Vector2D{
        if (m==null) return Vector2D(b, other.calc(b))
        if (other.m==null) {
            var x = other.b
            var y = calc(x)
            return Vector2D(x, y)
        }
        else{

            val x = (other.b-b)/(m-other.m)
            val y = calc(x)
            return Vector2D(x, y)

        }
    }

    /**
     * Computes the y-value of the function at a given x-value.
     * @param x The x-value.
     */
    fun calc(x: Double): Double{
        if (m==null) return b
        else return m*x+b
    }
    override fun toString(): String{
        if (m==null) return "LinearFunction x=$b"
        else return "LinearFunction y=${m}x+$b"
    }
}