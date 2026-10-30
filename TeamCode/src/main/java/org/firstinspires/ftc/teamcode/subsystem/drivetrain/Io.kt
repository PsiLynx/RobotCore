package org.firstinspires.ftc.teamcode.subsystem.drivetrain

import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemIo

interface Io: SubsystemIo {
    var position: Pose2D
    val velocity: Pose2D
    fun drivePowers(
        speeds: ChassisSpeeds = ChassisSpeeds()
    )
    val powers: ChassisSpeeds
    val acceleration: Pose2D
    fun resetLocalizer()
}