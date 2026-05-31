package org.firstinspires.ftc.teamcode.subsystem.tankDrivetrain

import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemIO

interface Io : SubsystemIO {
    fun differentialPowers(
        left: Double,
        right: Double,
        feedForward: Double = 0.0,
        comp: Boolean = false
    )
    val powers: ChassisSpeeds
    var position: Pose2D
    val velocity: Pose2D
    fun resetLocalizer()

}