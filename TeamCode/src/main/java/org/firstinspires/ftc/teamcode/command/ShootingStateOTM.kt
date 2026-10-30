package org.firstinspires.ftc.teamcode.command

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.shooter.ComputeTraj
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Range
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.geometry.valMap
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.shooter.ShooterConfig
import org.firstinspires.ftc.teamcode.shooter.CompTargets
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Beware! Even the action of looking and attempting to comprehend
 * these programs may cause minor yet persistent brain damage due to unnecessary
 * complexity!
 * You have been warned!
 *
 * This class is responsible for consoling the flywheel speed and the hood angle
 * Desmos graphs demonstrating the basic concepts can be found at
 * https://www.desmos.com/calculator/jaxgormzj1
 */
class ShootingStateOTM(
    var fromPos: () -> Pose2D = { Drivetrain.io.position },
    var botVel: () -> Pose2D = { Drivetrain.io.velocity },
    var target: () -> Vector3D = { CompTargets.compGoalPos(fromPos()) },
    var futureDT: Double = 0.1,
    // var futurePos: () -> Pose2D = { Drivetrain.futurePos(futureDT) },
) : Command() {

//    override val requirements = mutableSetOf<Subsystem<*,*>>(Hood, Flywheel, Turret)

    override fun initialize() {
        /** Using feedback sets the PID controller active. */
        Shooter.io.flywheelUsingFeedback = true
        Shooter.io.turretUsingFeedback = true
    }

    override fun execute() {

        val launchVec: Vector3D = compLaunchVec(
            target(),
            fromPos(),
            botVel(),
        )
//        val futureLaunchVec: Vector3D = compLaunchVec(
//            target(),
//            futurePos(),
//            botVel(),
//        )

        //now parse and command the flywheel, hood, and turret.
        if(!ShooterConfig.flywheelDisabled) {
//            Shooter.io.flywheelTargetState = VaState(
//                launchVec.mag,
//                (futureLaunchVec.mag - launchVec.mag) / futureDT
//            )

            Shooter.io.hoodTargetAngle = PI / 2 - launchVec.verticalAngle.toDouble()
        }
        else{
            Shooter.io.hoodTargetAngle = PI/2 - 30 * PI/180
            Shooter.io.flywheelTargetState = VaState(0.7,0.0)
        }

        if(!ShooterConfig.turretDisabled) {
            Shooter.io.turretTargetState = PvState(
                (
                        launchVec.horizontalAngle
                                - Drivetrain.io.position.heading
                        ).wrap(),

                -Drivetrain.io.velocity.heading
            )
        }
        else{
//            Shooter.io.turretMotors.forEach {
//                it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
//                it.power = 0.0
//            }
        }

//        log("targetVelocity") value launchVec.mag
//        log("launchAngle") value launchVec.verticalAngle
//
//        log("launchVec") value launchVec
//        log("FlywheelVelocityWithRBmotion") value Shooter.io.flywheelCurrentState.velocity
//        log("MovingVertAngle") value Shooter.io.hoodTargetAngle

    }

    override fun end(interrupted: Boolean){
        /**
         * Command flywheels to stop using feedback control.
         * Set flywheel power to 0 and hood angle to 0.
         */
        Shooter.io.flywheelUsingFeedback = false
        Shooter.io.turretUsingFeedback = false
//        Shooter.io.flywheelMotors.forEach { it.power = 0.0 }
//        Shooter.io.turretMotors.forEach { it.power = 0.0 }
//        Shooter.io.hoodSetAngle(Shooter.io.hoodMinAngle)
    }

    override var name = { "ShootingState" }

    fun compLaunchVec(goal: Vector3D, myPos: Pose2D, botVel: Pose2D): Vector3D{

        /**
         * compute the 2d path of the ball.
         */

        /**
         * Compute the point of the targetState with the flywheel at (0,0) and the targetState
         * all laying on a 2d plane.
         * Uses the Pythagorean formula for computing x.
         */

        val shooterOffset = (
            ShooterConfig.flywheelOffset.groundPlane
            rotatedBy myPos.heading
        )

        //println("shooter Pos$shooterOffset")

        val targetPoint2D = Vector2D(
            (goal.groundPlane - myPos.vector - shooterOffset).mag,
            goal.z - ShooterConfig.flywheelOffset.z
        )
        //println("targetPoint2D $targetPoint2D")

        val throughPoint = Vector2D(
            targetPoint2D.x + ShooterConfig.throughPointOffsetX,
            (
                goal.z + ShooterConfig.defaultThroughPointY
                - ShooterConfig.flywheelOffset.z
            )
        )
        //println("throughPoint $throughPoint")


        //println("targetPoint2D $targetPoint2D")

        val trajectory = ComputeTraj.computeTraj(throughPoint, targetPoint2D)
        var velocity = trajectory.first
        var launchAngle = trajectory.second

        var angleToGoal = atan2(
            goal.y - myPos.y - shooterOffset.y,
            goal.x - myPos.x - shooterOffset.x
        )

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