package org.ftc3825.command


import org.ftc3825.command.internal.Command
import org.ftc3825.pedroPathing.follower.Follower
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.Pose2D

class FollowPedroPath(var follower: Follower): Command() {
    override var requirements = arrayListOf<Subsystem<*>>(Drivetrain)
    override fun execute() {
        follower.update()
    }
    override fun end(interrupted: Boolean) {
        Drivetrain.setWeightedDrivePower(Pose2D())
    }

    override fun isFinished() = follower.isBusy
}