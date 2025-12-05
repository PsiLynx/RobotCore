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
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

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

    override fun initialize() {
        /** Using feedback sets the PID controller active. */
        Flywheel.usingFeedback = true
    }

    override fun execute() {

        println("SOTM time: " + measureTimedValue {
            //println("\nBelow are the SOTM debug printouts\n##############################")

            val goal = target()
            val myPos = fromPos()
            val botVel = botVel()

            val trajectory = myCalculator.compute(myPos, goal = goal)
            var velocity = trajectory.first
            var launchAngle = trajectory.second

            var angleToGoal = atan2(goal.y - myPos.y, goal.x - myPos.x)

            //println("fromPos ${myPos}")
            //println("target ${goal}")

            //println("Init velocity $velocity")
            //println("init launchAngle: ${launchAngle * 180 / PI}")

            //println("angleToGoal ${angleToGoal * 180 / PI}")

            //calculate the sides of the triangle baised from angle and hyp length.

            val velGroundPlane = cos(launchAngle) * velocity

            //println("heading ${launchAngle * 180 / PI}")
            //println("target angle ${Hood.targetAngle * 180 / PI}")
            //println("velGroundPlane $velGroundPlane")
            var vecX = cos(angleToGoal) * velGroundPlane
            var vecY = sin(angleToGoal) * velGroundPlane
            var vecZ = sin(launchAngle) * velocity

            var launchVec = Vector3D(vecX, vecY, vecZ)

            //println("launchVec1 $launchVec")

            //adjust for the motion of the drive base

            launchVec = launchVec - Vector3D(botVel.x, botVel.y, 0)
            //println("compensated launchVec $launchVec")
            //println("drivetrain velocity $botVel")

            //now parse and command the flywheel, hood, and turret.
            Flywheel.targetVelocity = launchVec.mag
            Hood.targetAngle = PI / 2 - launchVec.verticalAngle.toDouble()
            Turret.fieldCentricAngle = launchVec.horizontalAngle.toDouble()

            log("targetVelocity") value velocity
            log("launchAngle") value launchAngle

            log("launchVec") value launchVec
            log("FlywheelVelocityWithRBmotion") value Flywheel.velocity
            log("MovingVertAngle") value Hood.targetAngle
            log("MovingHeading") value Turret.fieldCentricAngle

            /*
            //debug printouts
            println("Velocity ${launchVec.mag}")
            println("launch Angle ${launchVec.verticalAngle.toDouble() * 180 / PI}")
            println("launch Heading W Motion ${launchVec.horizontalAngle * 180 / PI}")
            println("\nreading from the hardware drivers:\n")
            println("Velocity W motion ${Flywheel.targetVelocity}")
            println("launch Angle W motion ${Hood.targetAngle * 180 / PI}")
            println("launch Heading W Motion ${Turret.fieldCentricAngle * 180 / PI}")
            */
        }.duration.toString(DurationUnit.MICROSECONDS))
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
}