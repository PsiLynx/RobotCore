package org.firstinspires.ftc.teamcode.command

import com.acmerobotics.dashboard.config.Config
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
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
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
    var target: () -> Vector3D = { ShooterConfig.goalPose() },
    var futurePos: () -> Vector2D = { TankDrivetrain.futurePos(futureDT).vector},
    var futureDT: Double = 0.1,
    var throughPointOffset: Vector2D = ShooterConfig.defaultThroughPoint
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
            Pose2D(fromPos().x, fromPos().y),
            botVel(),
            throughPointOffset
        )
        val futureLaunchVec: Vector3D = compLaunchVec(
            target(),
            Pose2D(futurePos().x, futurePos().y),
            botVel(),
            throughPointOffset
        )

        //now parse and command the flywheel, hood, and turret.
        Flywheel.targetState = VaState(
            launchVec.mag,
            (futureLaunchVec.mag-launchVec.mag)/futureDT)

        Hood.targetAngle = PI/2 - launchVec.verticalAngle.toDouble()

        Turret.targetState = PvState(
            (
                launchVec.horizontalAngle
                - TankDrivetrain.position.heading
            ).wrap(),

            /*(
                launchVec.horizontalAngle
                - futureLaunchVec.horizontalAngle
            ) / futureDT */
            Rotation2D()
        )

        log("targetVelocity") value launchVec.mag
        log("launchAngle") value launchVec.verticalAngle

        log("launchVec") value launchVec
        log("FlywheelVelocityWithRBmotion") value Flywheel.currentState.velocity
        log("MovingVertAngle") value Hood.targetAngle

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

    fun compLaunchVec(goal: Vector3D, myPos: Pose2D, botVel: Pose2D, throughPoint: Vector2D): Vector3D{

        /**
         * compute the 2d path of the ball.
         */

        /**
         * Compute the point of the targetState with the flywheel at (0,0) and the targetState
         * all laying on a 2d plane.
         * Uses the Pythagorean formula for computing x.
         */

        val shooterOffset = Vector2D(
            cos(myPos.heading.toDouble())*ShooterConfig.flywheelOffset.x,
            sin(myPos.heading.toDouble())*ShooterConfig.flywheelOffset.y
        )
        //println("shooter Pos$shooterOffset")

        val targetPoint2D = Vector2D(
            (goal.groundPlane - myPos.vector - shooterOffset).mag,
            goal.z - ShooterConfig.flywheelOffset.z
        )

        //println("targetPoint2D $targetPoint2D")


        val trajectory = ComputeTraj.compute(throughPoint, targetPoint2D)
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

@Config object ShooterConfig {
    //Shooter globals:
    @JvmField var flywheelOffset = Vector3D(-1, 0, 13)
    @JvmField val flywheelRadius = 2.0
    @JvmField val ballOffset = Vector2D(-flywheelRadius - 2.5, 0) rotatedBy PI / 4
    val goalPose get() = {
        if(Globals.alliance == Globals.Alliance.RED) redGoal - Vector3D(
            Globals.artifactDiameter /2,
            Globals.artifactDiameter /2,0)
        else if(Globals.alliance == Globals.Alliance.BLUE) blueGoal - Vector3D(
            -Globals.artifactDiameter /2,
            Globals.artifactDiameter /2,0)
        else Vector3D()
    }


    @JvmField val defaultThroughPoint = Vector2D(-2,1)

    @JvmField val redGoal = Vector3D(64, 64, 40)
    @JvmField val blueGoal = Vector3D(-64, 64, 40)
}