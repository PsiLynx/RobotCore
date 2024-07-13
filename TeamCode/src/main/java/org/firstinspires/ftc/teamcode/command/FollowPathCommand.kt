package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.ThreeDeadWheelLocalizer
import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Rotation2D
import org.firstinspires.ftc.teamcode.util.inches

class FollowPathCommand(val localizer: ThreeDeadWheelLocalizer, val path: Path): Command() {
    init {
        addRequirement(Drivetrain)
    }

    override fun execute() {
        localizer.update()
        Drivetrain.setWeightedDrivePower(path.vector(localizer.position) + Rotation2D())
    }

    override fun isFinished(): Boolean {
        return (localizer.position.vector - path[-1].end).mag < inches(0.5)
    }

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( Pose2D(0, 0, 0) )
}