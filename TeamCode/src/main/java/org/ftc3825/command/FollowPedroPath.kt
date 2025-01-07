package org.ftc3825.command


import org.ftc3825.command.internal.Command
import org.ftc3825.pedroPathing.pathGeneration.Path
import org.ftc3825.pedroPathing.pathGeneration.PathBuilder
import org.ftc3825.pedroPathing.pathGeneration.PathChain
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.geometry.DrivePowers
import org.ftc3825.util.geometry.Pose2D

class FollowPedroPath(var path: PathChain): Command() {
    constructor(path: Path): this(PathBuilder().addPath(path).build())

    override var requirements = mutableSetOf<Subsystem<*>>(Drivetrain)


    override fun initialize(){
        println(path)
        Drivetrain.followPath(path)
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.setWeightedDrivePower(DrivePowers(0, 0, 0))
        Drivetrain.breakFollowing()
    }

    override fun isFinished() = !Drivetrain.isFollowing //|| (System.nanoTime() - start) > ( 5e9 )
    //override fun isFinished() = false

    override var name = "FollowPedroPath"
}
