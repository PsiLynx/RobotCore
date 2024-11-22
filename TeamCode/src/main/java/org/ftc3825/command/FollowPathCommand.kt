package org.ftc3825.command

import org.ftc3825.GVF.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import kotlin.math.abs

class FollowPathCommand(val path: Path): Command() {
    var pose = Pose2D()

    init {
        println(path)
        addRequirement(Drivetrain)
    }

    override fun execute() {
        pose = path.pose(Drivetrain.pos)
        Drivetrain.driveFieldCentric(
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
                && (Drivetrain.pos.vector - path[-1].end).mag < 0.4
                && abs(pose.heading) < 0.05
                && pose.vector.mag < 0.2
                //&& Drivetrain.delta.mag < 1e-1
        )
    }

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( Pose2D() )


    override var name = "FollowPathCommand"
    override var description = { path.toString() }
}
