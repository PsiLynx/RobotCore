package org.ftc3825.command

import org.ftc3825.GVF.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D

class FollowPathCommand(val path: Path): Command() {
    init {
        println(path)
        addRequirement(Drivetrain)
    }

    override fun execute() {
            val pose = path.pose(Drivetrain.position)
        Drivetrain.driveFeildCentric(
            Pose2D(
                pose.y,
                pose.x,
                pose.heading
            )
        )
    }

    override fun isFinished(): Boolean {
        return (
                path.index >= path.numSegments
                && (Drivetrain.position.vector - path[-1].end).mag < 0.5
                        && !Drivetrain.encoders.map { it.delta < 2}.contains(false)
        )
    }

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( Pose2D() )

    override fun toString() = "FollowPathCommand"
}
