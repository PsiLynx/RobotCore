package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.RamseteFollower
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.util.log
import kotlin.collections.flatten
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign

class RamseteCommand(
    val path: Path,
    val constants: RamseteConstants = RamseteConstants,
    val posConstraint: Double = 8.0,
    val velConstraint: Double = 1.0,
): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*,*>>(Drivetrain)

    val follower = RamseteFollower(constants)

    override fun initialize() {
        path.reset()
        path.initMP(
            constants.MAX_VELO,
            constants.A_MAX,
            constants.D_MAX,
        )
    }

    override fun execute() {
        follower.targetVel(
            path,
            Drivetrain.io.position,
            Drivetrain.io.velocity,
            Drivetrain.io.acceleration
        )
    }

    override fun isFinished() = (
        path.index >= path.numSegments - 1
        && (Drivetrain.io.position.vector - path[-1].end).mag < posConstraint
        && (
           Drivetrain.io.position.heading - path[-1].targetHeading(1.0)
       ).absoluteMag() < 0.4
        && (
            Drivetrain.io.velocity.vector.mag < velConstraint
            || path[-1].v_f > 0.2
        )
    )

    override fun end(interrupted: Boolean) =
        Drivetrain.io.drivePowers()

    fun withConstraints(
        posConstraint: Double = 8.0,
        velConstraint: Double = 1.0,
    ) = RamseteCommand(
        path, constants, posConstraint, velConstraint
    )


    override var name = { "RamseteCommand" }
    override var description = { path.toString() }
}
