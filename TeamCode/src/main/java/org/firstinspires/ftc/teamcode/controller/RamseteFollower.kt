package org.firstinspires.ftc.teamcode.controller

import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.RamseteConstants
import org.firstinspires.ftc.teamcode.util.log

class RamseteFollower(val constants: RamseteConstants) {
    val controller = WPILibRamseteController()

    fun targetVel(
        path: Path,
        pos: Pose2D,
        vel: Pose2D,
        acc: Pose2D,
    ): ChassisSpeeds {
        var targetPosVelAndAccel = path.targetPosVelAndAccel(
            pos
        )
        val forwardsVel = vel.vector.magInDirection(pos.heading)
        val forwardsAcc = acc.vector.magInDirection(pos.heading)

        val targetPosAndVel = PvState(
            targetPosVelAndAccel.first.vector
                    + targetPosVelAndAccel.first.heading.wrap(),

            targetPosVelAndAccel.second.vector
                    + targetPosVelAndAccel.second.heading * (
                    1 - (
                            targetPosVelAndAccel.second.vector.mag
                                    - vel.vector.mag
                            ) / constants.MAX_VELO
                    )
        )

        path.updateCurrent(
            path.currentPath.closestT(pos.vector)
        ) //TODO: Review

        val targetVel = (
            targetPosAndVel.velocity.vector.magInDirection(pos.heading)
        )

        val chassisSpeeds = controller.calculate(
            currentPose = pos.vector + pos.heading.wrap(),
            poseRef = targetPosAndVel.position,
            linearVelocityRefInches = targetVel,
            angularVelocityRefRadiansPerSecond = (
                targetPosAndVel.velocity.heading.toDouble()
            )
        )

        val drive = (
            VaState(
                ( chassisSpeeds.vy - forwardsVel ) / constants.MAX_VELO,
                forwardsAcc / constants.MAX_VELO
            ).applyPD(constants.DRIVE_P, constants.DRIVE_D).toDouble()

            + chassisSpeeds.vy / constants.MAX_VELO
            + constants.ACCEL_F * targetPosVelAndAccel.third.vector.mag
            + constants.DRIVE_Ks
        )
        //TODO: should target acc be inside the PD controller or not

        val turn = (
            VaState(
                (
                    Rotation2D(chassisSpeeds.vTheta)
                    - vel.heading
                ) / constants.MAX_HEADING_VELO,

                acc.heading / constants.MAX_HEADING_VELO
            ).applyPD(constants.HEADING_P, constants.HEADING_D).toDouble()

            + ( chassisSpeeds.vTheta / constants.MAX_HEADING_VELO )
            + (
                constants.HEADING_ACCEL_F
                * targetPosVelAndAccel.third.heading.mag
            )
            + constants.HEADING_Ks
        )
        //TODO: should target acc be inside the PD controller or not


        return ChassisSpeeds(
            0.0,
            drive,
            turn,
        )

        log("chassis speeds") value chassisSpeeds

        log("targetState pos in") value targetPosAndVel.position
        log("targetState vel inPerSec") value targetPosAndVel.velocity
        log("targetState vel double") value targetVel
        log("targetState vel rot radPerSec") value (
                targetPosAndVel.velocity.heading.toDouble()
                )
        log("trans accel") value targetPosVelAndAccel.third.y
        log("head accel") value (
            targetPosVelAndAccel.third.heading.mag
            / constants.MAX_VELO
        )
        log("index") value path.index
        log("end condition/position in") value (
            ( pos.vector - path[-1].end ).mag
        )
        log("end condition/velocity inPerSec") value (
            vel.vector.mag
        )
        log("end condition/requires vel") value (
            path[-1].v_f < 0.2
        )
        log("end condition/heading rad") value (
            pos.heading - path[-1].targetHeading(1.0)
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
}