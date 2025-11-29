package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.gvf.Line
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.DRIVE_D
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.DRIVE_P
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.HEADING_D
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.HEADING_P
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain.MAX_VELO
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain.MAX_HEADING_VELO
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


        val targetVel =
            targetPosAndVel.velocity.vector.magInDirection(
                TankDrivetrain.position.heading
            )

        val chassisSpeeds = controller.calculate(
            currentPose = TankDrivetrain.position,
            poseRef = targetPosAndVel.position,
            linearVelocityRefInches = targetVel,
            angularVelocityRefRadiansPerSecond = targetPosAndVel.velocity.heading.toDouble()
        )

        TankDrivetrain.setWeightedDrivePower(
            drive = PvState(
                (
                    chassisSpeeds.vy
                    - TankDrivetrain.forwardsVelocity
                ) / MAX_VELO,
                TankDrivetrain.forwardsAcceleration
            ).applyPD(
                DRIVE_P,
                DRIVE_D,
            ).toDouble() + chassisSpeeds.vy / MAX_VELO,
            turn = PvState(
                (
                    Rotation2D(chassisSpeeds.vTheta)
                        - TankDrivetrain.velocity.heading
                ) / MAX_HEADING_VELO,

                TankDrivetrain.acceleration.heading
            ).applyPD(
                HEADING_P,
                HEADING_D,
            ).toDouble() + chassisSpeeds.vTheta / MAX_HEADING_VELO,
        )

        log("chassis speeds") value chassisSpeeds

        log("target vel") value targetVel


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
