package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.P
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.D
import org.firstinspires.ftc.teamcode.gvf.Line
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.RamseteController
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.log
import kotlin.collections.flatten
import kotlin.math.abs

class RamseteCommand(
    val path: Path,
    val posConstraint: Double = 2.0,
    val velConstraint: Double = 5.0
): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

    val controller = RamseteController()

    override fun initialize() { path.reset() }

    override fun execute() {
        val targetPosAndVel = path.targetPosAndVel(TankDrivetrain.position)

        val chassisSpeeds = controller.calculate(
            TankDrivetrain.position,
            targetPosAndVel.position,
            targetPosAndVel.velocity.vector.magInDirection(
                TankDrivetrain.position.heading
            ),
            targetPosAndVel.velocity.heading.toDouble()

        )


        log("path") value (
                Array(path.numSegments) { it }.map { i ->
                    if(path[i] is Line) listOf<Pose2D>(
                        ( path[i].point(0.0) + Rotation2D() ),
                        ( path[i].point(1.0) + Rotation2D() )
                    )
                    else Array(11) {
                        (path[i].point(it / 10.0) + Rotation2D())
                    }.toList()
            }.flatten<Pose2D>().toTypedArray()
        )
        Array(path.numSegments) { it }.map { i ->
            log("path/segment $i") value (
                if (path[i] is Line) listOf<Pose2D>(
                    path[i].point(0.0) + Rotation2D(),
                    path[i].point(1.0) + Rotation2D()
                )
                else Array(11) {
                    (path[i].point(it / 10.0) + Rotation2D())
                }.toList()
            ).toTypedArray()
        }
    }

    override fun isFinished() = (
        path.index >= path.numSegments - 1
        && (TankDrivetrain.position.vector - path[-1].end).mag < posConstraint
        && abs(
           (
                   TankDrivetrain.position.heading - path[-1].targetHeading(1.0)
           ).toDouble()
        ) < 0.3
        && TankDrivetrain.velocity.mag < velConstraint
    )

    override fun end(interrupted: Boolean) =
        TankDrivetrain.setWeightedDrivePower()

    fun withConstraints(
        posConstraint: Double = 2.0,
        velConstraint: Double = 5.0
    ) = RamseteCommand(
        path, posConstraint, velConstraint
    )


    override var name = { "FollowPathCommand" }
    override var description = { path.toString() }
}
