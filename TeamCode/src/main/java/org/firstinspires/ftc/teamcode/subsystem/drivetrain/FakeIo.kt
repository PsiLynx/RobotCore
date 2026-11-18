package org.firstinspires.ftc.teamcode.subsystem.drivetrain

import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.log

class FakeIo : Io {
    override var position = Pose2D()
    override var velocity = Pose2D()
        private set

    override val robotCentricVelocity: ChassisSpeeds
        get(){
            val vel = velocity rotatedBy (-position.heading + Rotation2D(PI/2))
            return ChassisSpeeds(vel.x, vel.y, velocity.heading.toDouble())
        }


    override fun drivePowers(speeds: ChassisSpeeds) {
        powers = speeds
    }

    override var powers = ChassisSpeeds()
        private set
    override var acceleration = Pose2D()
        private set

    override fun resetLocalizer() { }

    override fun update(deltaTime: Double) {
        if(powers.vx > 1){
            println("[dt] powers.vx too big")
            powers = ChassisSpeeds(1.0, powers.vy, powers.vTheta)
        }
        if(powers.vx < -1){
            println("[dt] powers.vx too small")
            powers = ChassisSpeeds(-1.0, powers.vy, powers.vTheta)
        }

        if(powers.vy > 1){
            println("[dt] powers.vy too big")
            powers = ChassisSpeeds(powers.vx, 1.0, powers.vTheta)
        }
        if(powers.vy < -1){
            println("[dt] powers.vy too small")
            powers = ChassisSpeeds(powers.vx, -1.0, powers.vTheta)
        }

        if(powers.vTheta > 1){
            println("[dt] powers.vTheta too big")
            powers = ChassisSpeeds(powers.vx, powers.vy, 1.0)
        }
        if(powers.vTheta < -1){
            println("[dt] powers.vTheta too small")
            powers = ChassisSpeeds(powers.vx, powers.vy, -1.0)
        }
        Drivetrain.apply { log("real chassis speeds") value powers }
        // TODO: more accurate
        acceleration = Pose2D(
            Drivetrain.maxAccel * (
                powers.vx - (robotCentricVelocity.vx / Drivetrain.maxVel)
            ),
            Drivetrain.maxAccel * (
                powers.vy - (robotCentricVelocity.vy / Drivetrain.maxVel)
            ),
            Drivetrain.maxHeadingAccel * (
                powers.vTheta
                - (velocity.heading.toDouble() / Drivetrain.maxHeadingVel)
            )
        ) rotatedBy ( position.heading - Rotation2D(PI/2) )
        velocity += acceleration * deltaTime
        position += velocity * deltaTime
    }

    override fun reset() { }
}