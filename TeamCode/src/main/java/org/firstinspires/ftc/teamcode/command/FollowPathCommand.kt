package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.ThreeDeadWheelLocalizer
import org.firstinspires.ftc.teamcode.util.Rotation2D

class FollowPathCommand(val drivetrain: Drivetrain, val localizer: ThreeDeadWheelLocalizer, val path: Path): Command() {
    init {
        addReqirements(drivetrain)
        //NOTE: localizer does not need to be taken away from other commands.
    }

    override fun execute() {
        localizer.update()
        println(localizer.position.vector)
        drivetrain.setWeightedDrivePower(path.vector(localizer.position) + Rotation2D())
    }

    override fun isFinished(): Boolean {
        return (localizer.position.vector - path[-1].end).magSq < 0.25
    }
}