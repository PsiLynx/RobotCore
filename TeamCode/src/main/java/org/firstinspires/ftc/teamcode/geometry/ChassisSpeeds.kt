package org.firstinspires.ftc.teamcode.geometry

/**
 * represents robot centric velocity.
 *  +y is up, +x is right
 *  @param vx velocity to the right of the robot
 *  @param vy velocity to the front of the robot
 *  @param vTheta CCW angular velocity
 */
data class ChassisSpeeds(
    val vx: Double = 0.0,
    val vy: Double = 0.0,
    val vTheta: Double = 0.0
){

    operator fun unaryPlus() = ChassisSpeeds(vx, vy, vTheta)

    operator fun unaryMinus() = ChassisSpeeds(-vx, -vy, -vTheta)

    operator fun plus(other: ChassisSpeeds) = ChassisSpeeds(
        vx + other.vx,
        vy + other.vy,
        vTheta + other.vTheta
    )

    operator fun minus(other: ChassisSpeeds) = this + (-other)

    operator fun times(other: Number) = ChassisSpeeds(
        vx     * other.toDouble(),
        vy     * other.toDouble(),
        vTheta * other.toDouble(),
    )
    operator fun div(other: Number) = ChassisSpeeds(
        vx     / other.toDouble(),
        vy     / other.toDouble(),
        vTheta / other.toDouble(),
    )
}