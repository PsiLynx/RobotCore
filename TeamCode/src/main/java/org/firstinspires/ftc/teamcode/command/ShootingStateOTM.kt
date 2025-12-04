package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.trajcode.ComputeTraj
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * This class is responcible for conroling the flywheel speed and the hood angle
 * Desmos graphs demonstrating the basic concepts can be found at
 * https://www.desmos.com/calculator/jaxgormzj1
 */
class ShootingStateOTM(
    var fromPos: () -> Vector2D = { Drivetrain.position.vector },
    var botVel: () -> Pose2D = { Drivetrain.velocity },
    var target: () -> Vector3D = {Globals.goalPose},
    var throughPointOffset: Vector2D = Globals.throughPoint
) : Command() {

    override val requirements = mutableSetOf<Subsystem<*>>(Hood, Flywheel, Turret)
    var myCalculator = ComputeTraj(throughPointOffset = throughPointOffset)

    var loopNum = 0
    var startTime: Long =0
    var lastTime: Long = 0

    val times: MutableList<Long> = mutableListOf()


    override fun initialize() {
        /** Using feedback sets the PID controller active. */
        Flywheel.usingFeedback = true
    }

    override fun execute() {

        startTime = System.nanoTime()
        lastTime = System.nanoTime()

        println("\nBelow are the SOTM debug printouts\n##############################")

        testFunc()

        val goal = target()
        val myPos = fromPos()
        val botVel = botVel()

        testFunc()

        val trajectory = myCalculator.compute(myPos, goal = goal)
        var velocity = trajectory.first
        var launchAngle = trajectory.second

        testFunc()

        var angleToGoal = atan2(goal.y - myPos.y, goal.x - myPos.x)

        println("fromPos ${myPos}")
        println("target ${goal}")

        println("Init velocity $velocity")
        println("init launchAngle: ${launchAngle*180/PI}")

        println("angleToGoal ${angleToGoal*180/PI}")

        //calculate the sides of the triangle baised from angle and hyp length.
        testFunc()

        val velGroundPlane = cos(launchAngle) * velocity

        println("heading ${launchAngle*180/PI}")
        println("target angle ${Hood.targetAngle*180/PI}")
        println("velGroundPlane $velGroundPlane")
        var vecX = cos(angleToGoal)*velGroundPlane
        var vecY = sin(angleToGoal)*velGroundPlane
        var vecZ = sin(launchAngle) * velocity

        var launchVec = Vector3D(vecX, vecY, vecZ)

        println("launchVec1 $launchVec")

        testFunc()

        //adjust for the motion of the drive base

        launchVec = launchVec - Vector3D(botVel.x, botVel.y, 0)
        println("compensated launchVec $launchVec")
        println("drivetrain velocity $botVel")

        testFunc()

        //now parse and command the flywheel, hood, and turret.
        Flywheel.targetVelocity = launchVec.mag
        Hood.targetAngle = PI/2 - launchVec.verticalAngle.toDouble()
        Turret.fieldCentricAngle = launchVec.horizontalAngle.toDouble()

        testFunc()

        log("targetVelocity") value velocity
        log("launchAngle") value launchAngle

        log("launchVec") value launchVec
        log("FlywheelVelocityWithRBmotion") value Flywheel.velocity
        log("MovingVertAngle") value Hood.targetAngle
        log("MovingHeading") value Turret.fieldCentricAngle

        testFunc()

        val total = System.nanoTime() - startTime
        println("Total time: ${total / 1_000_000.0} ms")
        println("${times.sum() / 1_000_000.0}")

        for ((index, time) in times.withIndex()) {
            val percentage = (time.toDouble() / total) * 100.0
            println("Section $index: ${"%.2f".format(percentage)}%")
        }

    }

    override fun end(interrupted: Boolean){
        /**
         * Command flywheels to stop using feedback control.
         * Set flywheel power to 0 and hood angle to 0.
         */
        Flywheel.usingFeedback = false
        Flywheel.motors.forEach { it.power = 0.0 }
        Hood.setAngle(Hood.minAngle)
    }

    override var name = { "ShootingState" }

    fun testFunc(){
        times.add(System.nanoTime() - lastTime)
        lastTime = System.nanoTime()

    }
}