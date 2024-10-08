package org.ftc3825.command

import org.ftc3825.GVF.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.LocalizerSubsystem
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.inches

class FollowPathCommand(val path: Path): Command() {
    init {
        addRequirement(Drivetrain)
        addRequirement(LocalizerSubsystem, write = false)
    }

    override fun execute() {
        LocalizerSubsystem.update()
        Drivetrain.setWeightedDrivePower(path.vector(LocalizerSubsystem.position) + Rotation2D())
    }

    override fun isFinished(): Boolean {
        return (LocalizerSubsystem.position.vector - path[-1].end).mag < 0.5
    }

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( Pose2D(0, 0, 0) )
}