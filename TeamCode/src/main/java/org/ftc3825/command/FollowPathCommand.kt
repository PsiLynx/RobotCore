package org.ftc3825.command

import org.ftc3825.gvf.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D

class FollowPathCommand(val path: Path): Command() {
    init {
        println(path)
        addRequirement(Drivetrain)
    }
    var power = Pose2D()
        internal set

    override fun execute() {
        power = path.pose(Drivetrain.position, Drivetrain.velocity)
        Drivetrain.driveFieldCentric(power)
    }

    override fun isFinished(): Boolean {
        return (
                path.index >= path.numSegments
                && (Drivetrain.position.vector - path[-1].end).mag < 0.4
        )
    }

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( Pose2D() )


    override var name = "FollowPathCommand"
    override var description = { path.toString() }
}
