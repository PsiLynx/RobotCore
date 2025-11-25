package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.geometry.ComputeTraj
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.PI

/**
 * This class is responcible for conroling the flywheel speed and the hood angle
 * math graphs can be found at https://www.desmos.com/calculator/jaxgormzj1
 */
class ShootingState(
    var fromPos: () -> Vector2D,
    var target: Vector3D = Globals.goalPose,
    var throughPointOffset: Vector2D = Vector2D(-17, 15)
) : Command() {

    override val requirements = mutableSetOf<Subsystem<*>>(Hood, Flywheel)
    
    var myCalculator = ComputeTraj(throughPointOffset = throughPointOffset, goal = target)

    override fun initialize() {
        /** Using feedback sets the PID controller active. */
        Flywheel.usingFeedback = true
    }

    override fun execute() {

        val traj = myCalculator.compute(fromPos())
        var velocity = traj.first
        var launchAngle = traj.second

        Flywheel.targetVelocity = velocity
        Hood.targetAngle = PI/2 - launchAngle

        println("launchAngle: "+(launchAngle * 180 / PI))
        println("velocity: $velocity")

        println("\nReading from the hardware\n")

        println("targetVelocity: ${Flywheel.targetVelocity}")
        println("targetAngle: ${Hood.targetAngle*180/PI}")


        log("targetVelocity") value velocity
        log("launchAngle") value launchAngle*180/PI
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