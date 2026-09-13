package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.RamseteFollower
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain

class RamseteCommand(
    val path: Path,
    val constants: RamseteConstants,
    val posConstraint: Double = 8.0,
    val velConstraint: Double = 1.0,
): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

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
    ) = RamseteCommand(
        path, constants, posConstraint, velConstraint
    )


    override var name = { "RamseteCommand" }
    override var description = { path.toString() }
}
