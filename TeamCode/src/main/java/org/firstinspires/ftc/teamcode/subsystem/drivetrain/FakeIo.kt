package org.firstinspires.ftc.teamcode.subsystem.drivetrain

import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D

class FakeIo : Io {
    override var position: Pose2D
        get() = TODO("Not yet implemented")
        set(value) {}
    override val velocity: Pose2D
        get() = TODO("Not yet implemented")

    override fun drivePowers(speeds: ChassisSpeeds) {
        TODO("Not yet implemented")
    }

    override val powers: ChassisSpeeds
        get() = TODO("Not yet implemented")
    override val acceleration: Pose2D
        get() = TODO("Not yet implemented")

    override fun resetLocalizer() {
        TODO("Not yet implemented")
    }

    override fun update(deltaTime: Double) {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }
}