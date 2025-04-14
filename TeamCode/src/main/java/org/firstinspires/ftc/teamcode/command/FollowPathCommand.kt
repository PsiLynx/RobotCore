package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.FEED_FORWARD
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.USE_COMP
import org.firstinspires.ftc.teamcode.util.Drawing
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import kotlin.math.abs

class FollowPathCommand(
    val path: Path,
    val posConstraint: Double = 2.0,
    val velConstraint: Double = 5.0
): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)

    var power = Pose2D()
        internal set

    override fun initialize() {
        path.reset()
        Drawing.drawGVFPath(path, false)
        Drawing.drawLine(
            Drivetrain.position.x,
            Drivetrain.position.y,
            power.vector.theta.toDouble(),
            "black"
        )
    }
    override fun execute() {
        val powers = path.powers(Drivetrain.position, Drivetrain.velocity)
        power = powers.fold(Pose2D()) { acc, it -> acc + it }

        Drivetrain.fieldCentricPowers(powers, FEED_FORWARD, USE_COMP)
        Drawing.drawGVFPath(path, true)
//        Drawing.drawLine(
//            Drivetrain.position.x,
//            Drivetrain.position.y,
//            power.vector.theta.toDouble(),
//            "black"
//        )
    }

    override fun isFinished() = (
        path.index >= path.numSegments - 1
        && (Drivetrain.position.vector - path[-1].end).mag < posConstraint
        && abs(
           (
               Drivetrain.position.heading - path[-1].targetHeading(1.0)
           ).toDouble()
        ) < 0.3
        && Drivetrain.velocity.mag < velConstraint
    )

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower()

    fun withConstraints(
        posConstraint: Double = 2.0,
        velConstraint: Double = 5.0
    ) = FollowPathCommand(
        path, posConstraint, velConstraint
    )


    override var name = { "FollowPathCommand" }
    override var description = { path.toString() }
}
