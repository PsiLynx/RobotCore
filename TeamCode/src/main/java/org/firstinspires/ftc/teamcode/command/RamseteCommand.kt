package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.RamseteFollower
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.DRIVE_D
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.DRIVE_Ks
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.DRIVE_P
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.HEADING_D
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.HEADING_Ks
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.HEADING_P
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain.MAX_VELO
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain.MAX_HEADING_VELO
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig.Ks
import org.firstinspires.ftc.teamcode.controller.WPILibRamseteController
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.log
import kotlin.collections.flatten
import kotlin.math.cos

class RamseteCommand(
    val path: Path,
    val constants: RamseteConstants,
    val posConstraint: Double = 8.0,
    val velConstraint: Double = 1.0,
    val aMax: Double = RamseteConstants.A_MAX,
    val dMax: Double = RamseteConstants.D_MAX,
    val maxVel: Double = MAX_VELO,
): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

    val follower = RamseteFollower(constants)

    override fun initialize() {
        path.reset()
        path.initMP(
            maxVel,
            aMax,
            dMax
        )
    }

    override fun execute() {
        follower.targetVel(
            path,
            TankDrivetrain.position,
            TankDrivetrain.velocity,
            TankDrivetrain.acceleration
        )
    }

    override fun isFinished() = (
        path.index >= path.numSegments - 1
        && (TankDrivetrain.position.vector - path[-1].end).mag < posConstraint
        && (
           TankDrivetrain.position.heading - path[-1].targetHeading(1.0)
       ).absoluteMag() < 0.4
        && (
            TankDrivetrain.velocity.vector.mag < velConstraint
            || path[-1].v_f > 0.2
        )
    )

    override fun end(interrupted: Boolean) =
        TankDrivetrain.setWeightedDrivePower()

    fun withConstraints(
        posConstraint: Double = 8.0,
        velConstraint: Double = 1.0,
        aMax: Double = RamseteConstants.A_MAX,
        dMax: Double = RamseteConstants.D_MAX,
        maxVel: Double = MAX_VELO,
    ) = RamseteCommand(
        path, constants, posConstraint, velConstraint, aMax, dMax, maxVel
    )


    override var name = { "RamseteCommand" }
    override var description = { path.toString() }
}
