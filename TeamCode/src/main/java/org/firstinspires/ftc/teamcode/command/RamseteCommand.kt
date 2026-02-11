package org.firstinspires.ftc.teamcode.command

import kotlinx.coroutines.currentCoroutineContext
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.mp.TrapMpParams
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.GVFConstants
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.DRIVE_D
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.DRIVE_P
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.HEADING_D
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants.HEADING_P
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain.MAX_VELO
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain.MAX_HEADING_VELO
import org.firstinspires.ftc.teamcode.gvf.RamseteController
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain.velocity
import org.firstinspires.ftc.teamcode.util.log
import kotlin.collections.flatten
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign

class RamseteCommand(
    val path: Path,
    val posConstraint: Double = 8.0,
    val velConstraint: Double = 1.0,
    val aMax: Double = RamseteConstants.A_MAX,
    val dMax: Double = RamseteConstants.D_MAX,
    val maxVel: Double = MAX_VELO,
): Command() {
    init { println(path) }

    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

    val controller = RamseteController()

    override fun initialize() { path.reset() }

    override fun execute() {
        var targetPosVelAndAccel = path.targetPosVelAndAccel(
            TankDrivetrain.position,
            MAX_VELO,
            aMax,
            dMax,
        )

        val targetPosAndVel = PvState(
            targetPosVelAndAccel.first.vector
            + targetPosVelAndAccel.first.heading.wrap(),

            targetPosVelAndAccel.second.vector
            + targetPosVelAndAccel.second.heading * (
                1 - (
                    targetPosVelAndAccel.second.vector.mag
                    - TankDrivetrain.velocity.vector.mag
                ) / MAX_VELO
            )
        )

        path.updateCurrent(
            path.currentPath.closestT(TankDrivetrain.position.vector)
        )

        val targetVel = (
            cos((
                targetPosAndVel.velocity.vector.theta
                - TankDrivetrain.position.heading
            ).toDouble())
            * targetPosAndVel.velocity.vector.mag
        )

        val chassisSpeeds = controller.calculate(
            currentPose = (
                TankDrivetrain.position.vector
                + TankDrivetrain.position.heading.wrap()
            ),
            poseRef = targetPosAndVel.position,
            linearVelocityRefInches = targetVel,
            angularVelocityRefRadiansPerSecond = targetPosAndVel.velocity.heading.toDouble()
        )

        var drive = PvState(
            (
                chassisSpeeds.vy
                - TankDrivetrain.forwardsVelocity
            ) / MAX_VELO,
            TankDrivetrain.forwardsAcceleration / MAX_VELO
        ).applyPD(
            DRIVE_P,
            DRIVE_D,
        ).toDouble() + chassisSpeeds.vy / MAX_VELO

        var turn = (
            PvState(
                (
                    Rotation2D(chassisSpeeds.vTheta)
                    - TankDrivetrain.velocity.heading
                ) / MAX_HEADING_VELO,

                TankDrivetrain.acceleration.heading / MAX_HEADING_VELO
            ).applyPD(
                HEADING_P,
                HEADING_D,
            ).toDouble()
            + (
                chassisSpeeds.vTheta
                / MAX_HEADING_VELO
                * RamseteConstants.HEADING_F
            )
        )

        val closestT = path.currentPath.closestT(
            TankDrivetrain.position.vector
        )
        if(
            targetPosVelAndAccel.third == TrapMpParams.State.ACCEL
        ){
            drive += RamseteConstants.ACCEL_F * (
                if(drive < 0) -1
                else 1
            )
        }
        else if(
            targetPosVelAndAccel.third == TrapMpParams.State.DECCEL
            && closestT <= 0.8
        ){
            drive += RamseteConstants.ACCEL_F * (
                if(drive < 0) 1
                else -1
            )
        }

        TankDrivetrain.setWeightedDrivePower(
            drive,
            turn,
            RamseteConstants.FEED_FORWARD,
            true
        )

        log("chassis speeds") value chassisSpeeds

        log("targetState pos") value targetPosAndVel.position
        log("targetState vel") value targetPosAndVel.velocity
        log("targetState vel double") value targetVel
        log("targetState vel rot") value targetPosAndVel.velocity.heading.toDouble()
        log("target velocity (m)") value targetPosAndVel.velocity.mag / 39.37
        log("actual velocity (m)") value TankDrivetrain.velocity.mag / 39.37
        log("index") value path.index
        log("end condition/position") value (
            ( TankDrivetrain.position.vector - path[-1].end ).mag
        )
        log("end condition/velocity") value (
            TankDrivetrain.velocity.vector.mag
        )
        log("end condition/requires vel") value (
            path[-1].v_f < 0.2
        )
        log("end condition/heading") value (
            TankDrivetrain.position.heading - path[-1].targetHeading(1.0)
        ).absoluteMag().toDouble()


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
        && (
           TankDrivetrain.position.heading - path[-1].targetHeading(1.0)
       ).absoluteMag() < 0.4
        && (
            TankDrivetrain.velocity.vector.mag < velConstraint
            || path[-1].v_f > 0.2
        )
    )

    override fun end(interrupted: Boolean) =
        TankDrivetrain.setWeightedDrivePower()

    fun withConstraints(
        posConstraint: Double = 8.0,
        velConstraint: Double = 1.0,
        aMax: Double = RamseteConstants.A_MAX,
        dMax: Double = RamseteConstants.D_MAX,
        maxVel: Double = MAX_VELO,
    ) = RamseteCommand(
        path, posConstraint, velConstraint, aMax, dMax, maxVel
    )


    override var name = { "RamseteCommand" }
    override var description = { path.toString() }
}
