package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.FEED_FORWARD
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.USE_COMP
import org.firstinspires.ftc.teamcode.gvf.Line
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.collections.flatten
import kotlin.math.PI
import kotlin.math.abs

class FollowPathCommand(
    val path: Path,
    val posConstraint: Double = 4.0,
    val velConstraint: Double = 2.0,
    val headConstraint: Double = 0.3
): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)

    var power = Pose2D()
        private set

    override fun initialize() {
        path.reset()
        power = Pose2D()

    }
    override fun execute() {
        val powers = path.powers(Drivetrain.position, Drivetrain.velocity)
        power = powers.fold(Pose2D()) { acc, it -> acc + it }

        Drivetrain.fieldCentricPowers(powers, FEED_FORWARD, USE_COMP)
        log("path") value (
                Array(path.numSegments) { it }.map { i ->
                    Array(11) {
                        (
                            path[i].point(it / 10.0)
                            + path[i].targetHeading(it / 10.0)
                        )
                    }.toList()
            }.flatten<Pose2D>().toTypedArray()
        )
        Array(path.numSegments) { it }.map { i ->
            log("path/segment $i") value (
                Array(11) {
                    (
                        path[i].point(it / 10.0)
                        + path[i].targetHeading(it / 10.0)
                    )
                }.toList()
            ).toTypedArray()
        }
    }
    override fun isFinished() = (
        path.index >= path.numSegments - 1
        && (Drivetrain.position.vector - path[-1].end).mag < posConstraint
        /*&& abs(
                Drivetrain.position.heading.toDouble()
                - path[-1].targetHeading(1.0).toDouble(),
        ) < headConstraint*/
        && Drivetrain.velocity.mag < velConstraint
    )

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower()

    fun withConstraints(
        posConstraint: Double = 4.0,
        velConstraint: Double = 2.0,
        headConstraint: Double = 0.3
    ) = FollowPathCommand(
        path, posConstraint, velConstraint, headConstraint
    )


    override var name = { "FollowPathCommand" }
    override var description = { path.toString() }
}
