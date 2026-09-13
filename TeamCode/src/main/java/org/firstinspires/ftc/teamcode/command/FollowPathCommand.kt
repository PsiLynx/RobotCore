package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.FEED_FORWARD
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.USE_COMP
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.log
import kotlin.collections.flatten

class FollowPathCommand(
    val path: Path,
    val posConstraint: Double = 4.0,
    val velConstraint: Double = 2.0,
    val headConstraint: Double = 0.3
): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

    var power = Pose2D()
        private set

    override fun initialize() {
        path.reset()
        power = Pose2D()

    }
    override fun execute() {
    }
    override fun isFinished() = (
        path.index >= path.numSegments - 1
        && (TankDrivetrain.position.vector - path[-1].end).mag < posConstraint
        /*&& abs(
                TankDrivetrain.position.heading.toDouble()
                - path[-1].targetHeading(1.0).toDouble(),
        ) < headConstraint*/
        && TankDrivetrain.velocity.mag < velConstraint
    )

    override fun end(interrupted: Boolean) =
        TankDrivetrain.setWeightedDrivePower()

    fun withConstraints(
        posConstraint: Double = 4.0,
        velConstraint: Double = 2.0,
        headConstraint: Double = 0.3
    ) = FollowPathCommand(
        path, posConstraint, velConstraint, headConstraint
    )


    override var name = { "FollowPathCommand" }
    override var description = { path.toString() }
}
