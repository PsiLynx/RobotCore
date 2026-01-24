package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.trajcode.ComputeTraj
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
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
    var fromPos: () -> Vector2D = { TankDrivetrain.position.vector },
    var botVel: () -> Pose2D = { TankDrivetrain.velocity },
    var target: () -> Vector3D = {Globals.goalPose},
    var futurePos: () -> Vector2D = { TankDrivetrain.futurePos(futureDT).vector},
    var futureDT: Double = 0.1,
    var throughPointOffset: Vector2D = Globals.throughPoint
) : Command() {

    override val requirements = mutableSetOf<Subsystem<*>>(Hood, Flywheel, Turret)

    override fun initialize() {
        /** Using feedback sets the PID controller active. */
        Flywheel.usingFeedback = true
        Turret.usingFeedback = true
    }

    override fun execute() {

        val launchVec: Vector3D = compLaunchVec(
            target(),
            fromPos(),
            botVel(),
            throughPointOffset
        )
        val futureLaunchVec: Vector3D = compLaunchVec(
            target(),
            futurePos(),
            botVel(),
            throughPointOffset
        )

        //now parse and command the flywheel, hood, and turret.
        Flywheel.targetState = VaState(
            launchVec.mag,
            (futureLaunchVec.mag-launchVec.mag)/futureDT)

        Hood.targetAngle = PI/2 - launchVec.verticalAngle.toDouble()

        Turret.targetState = PvState(
            launchVec.horizontalAngle - TankDrivetrain.position.heading,

            (
                launchVec.horizontalAngle
                - futureLaunchVec.horizontalAngle
            ) / futureDT
        )

        log("targetVelocity") value launchVec.mag
        log("launchAngle") value launchVec.verticalAngle

        log("launchVec") value launchVec
        log("FlywheelVelocityWithRBmotion") value Flywheel.currentState.velocity
        log("MovingVertAngle") value Hood.targetAngle
        log("MovingHeading") value Turret.fieldCentricAngle

    }

    override fun end(interrupted: Boolean){
        /**
         * Command flywheels to stop using feedback control.
         * Set flywheel power to 0 and hood angle to 0.
         */
        Flywheel.usingFeedback = false
        Turret.usingFeedback = false
        Flywheel.motors.forEach { it.power = 0.0 }
        Turret.motors.forEach { it.power = 0.0 }
        Hood.setAngle(Hood.minAngle)
    }

    override var name = { "ShootingState" }

    fun compLaunchVec(goal: Vector3D, myPos: Vector2D, botVel: Pose2D, throughPoint: Vector2D): Vector3D{

        val trajectory = ComputeTraj.compute(myPos, goal, throughPoint)
        var velocity = trajectory.first
        var launchAngle = trajectory.second

        var angleToGoal = atan2(goal.y - myPos.y, goal.x - myPos.x)

        //calculate the sides of the triangle baised from angle and hyp length.

        val velGroundPlane = cos(launchAngle) * velocity

        var vecX = cos(angleToGoal) * velGroundPlane
        var vecY = sin(angleToGoal) * velGroundPlane
        var vecZ = sin(launchAngle) * velocity

        var launchVec = Vector3D(vecX, vecY, vecZ)

        //adjust for the motion of the drive base
        launchVec = launchVec - Vector3D(botVel.x, botVel.y, 0)


        return launchVec
    }

}