package org.ftc3825.command

import org.ftc3825.gvf.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.gvf.GVFConstants.FEED_FORWARD
import org.ftc3825.util.Drawing
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.geometry.DrivePowers
import org.ftc3825.util.geometry.Pose2D
import kotlin.math.abs

class FollowPathCommand(val path: Path): Command() {
    init {
        println(path)
        addRequirement(Drivetrain)
    }
    var power = Pose2D()
        internal set

    override fun execute() {
        power = path.pose(Drivetrain.position, Drivetrain.velocity)
        Drivetrain.driveFieldCentric(power, FEED_FORWARD)
        Drawing.drawGVFPath(path, true)
        Drawing.drawLine(Drivetrain.position.x, Drivetrain.position.y, power.vector.theta.toDouble(), "black")
    }

    override fun isFinished() = (
        path.index >= path.numSegments
        && (Drivetrain.position.vector - path[-1].end).mag < 0.4
        && abs((
            Drivetrain.position.heading
            - path[-1].targetHeading(1.0)
        ).toDouble()) < 0.3
        && Drivetrain.velocity.mag < 0.1

    )

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( DrivePowers(0, 0, 0) )


    override var name = "FollowPathCommand"
    override var description = { path.toString() }
}
