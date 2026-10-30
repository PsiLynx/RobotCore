package org.firstinspires.ftc.teamcode.subsystem.drivetrain

import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D

class FakeIo : Io {
    override var position: Pose2D = Pose2D()
    override var velocity: Pose2D = Pose2D()
        private set


    override fun drivePowers(speeds: ChassisSpeeds) {
        powers = speeds
    }

    override var powers = ChassisSpeeds()
        private set
    override var acceleration = Pose2D()
        private set

    override fun resetLocalizer() { }

    override fun update(deltaTime: Double) {
        acceleration = Pose2D(
            Drivetrain.MAX_ACCELERATION * (powers.vx - (velocity.x / Drivetrain.MAX_VELOCITY)),
            Drivetrain.MAX_ACCELERATION * (powers.vy - (velocity.y / Drivetrain.MAX_VELOCITY)),
            Drivetrain.MAX_HEADING_ACCELERATION * (powers.vTheta - (velocity.heading.toDouble() / Drivetrain.MAX_HEADING_VELOCITY))
        ) rotatedBy position.heading
        velocity += acceleration * deltaTime
        position += velocity * deltaTime
    }

    override fun reset() { }
}