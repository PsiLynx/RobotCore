package org.ftc3825.command


import org.ftc3825.command.internal.Command
import org.ftc3825.pedroPathing.pathGeneration.Path
import org.ftc3825.pedroPathing.pathGeneration.PathBuilder
import org.ftc3825.pedroPathing.pathGeneration.PathChain
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.Pose2D

class FollowPedroPath(var path: PathChain): Command() {
    constructor(path: Path): this(PathBuilder().addPath(path).build())

    override var requirements = mutableSetOf<Subsystem<*>>(Drivetrain)

    var start = 0L

    override fun initialize(){
        println(path)
        Drivetrain.followPath(path)
        start = System.nanoTime()

    }

    override fun end(interrupted: Boolean) {
        Drivetrain.setWeightedDrivePower(Pose2D())
        Drivetrain.breakFollowing()
    }

    override fun isFinished() = !Drivetrain.isFollowing //|| (System.nanoTime() - start) > ( 5e9 )
    //override fun isFinished() = false

    override var name = "FollowPedroPath"
}
