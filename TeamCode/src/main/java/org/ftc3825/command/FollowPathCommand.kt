package org.ftc3825.command

import org.ftc3825.gvf.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.gvf.GVFConstants.FEED_FORWARD
import org.ftc3825.util.Drawing
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.geometry.Pose2D
import kotlin.math.abs

class FollowPathCommand(val path: Path): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)

    var power = Pose2D()
        internal set

    override fun initialize() {
        path.index = 0
        Telemetry.addFunction("dist") {
            ( path[-1].end - Drivetrain.position.vector ).mag
        }
        Drawing.drawGVFPath(path, false)
        Drawing.drawLine(Drivetrain.position.x, Drivetrain.position.y, power.vector.theta.toDouble(), "black")
        Drawing.sendPacket()
    }
    override fun execute() {
        power = path.pose(Drivetrain.position, Drivetrain.velocity)
        Drivetrain.driveFieldCentric(power, FEED_FORWARD, comp = true)
        Drawing.drawGVFPath(path, true)
        Drawing.drawLine(Drivetrain.position.x, Drivetrain.position.y, power.vector.theta.toDouble(), "black")
    }

    override fun isFinished() = (
        path.index >= path.numSegments
        && (Drivetrain.position.vector - path[-1].end).mag < 1.0
        && abs((
            Drivetrain.position.heading
            - path[-1].targetHeading(1.0)
        ).toDouble()) < 0.3
        && Drivetrain.velocity.mag < 0.2

    )

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower()


    override var name = { "FollowPathCommand" }
    override var description = { path.toString() }
}
