package org.firstinspires.ftc.teamcode.command

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.util.CacheValue
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

@Config object ShootingConf {
    @JvmField var targetDepth = 1
    @JvmField var targetHeight = 7.5
    @JvmField var targetSlope = 0.5

    var flywheelOffset = Vector3D(0, 0.5, 12)
    var flywheelRadius = 1.5
    var flywheelEntrancePhi = degrees(-35)
}

class ShootingStateOTM(
    var fromPos: () -> Pose2D = { Drivetrain.io.position },
    var botVel: () -> Pose2D = { Drivetrain.io.velocity },
    var futureDT: Double = 0.1,
    // var futurePos: () -> Pose2D = { Drivetrain.futurePos(futureDT) },
) : Command() {

    override val requirements = mutableSetOf<Subsystem<*, *>>(Shooter)

    var shotTime = CacheValue(0.0, -1e10)

    override fun initialize() {
        /** Using feedback sets the PID controller active. */
        Shooter.io.flywheelUsingFeedback = true
        Shooter.io.turretUsingFeedback = true
    }

    override fun execute() {

        var reps = 3

        var time = if (shotTime.age() > 0.5) {
            reps *= 10
            0.0
        } else shotTime.value

        repeat(reps) {
            time = getShotTime(
                Drivetrain.io.position.vector,
                Drivetrain.io.velocity.vector,
                ShootingConf.targetSlope,
                time
            )!!
        }
        shotTime = CacheValue(time)

        val solution = solveShot(
            Drivetrain.io.position.vector,
            Drivetrain.io.velocity.vector,
            ShootingConf.targetSlope,
            time
        )!!
        Shooter.io.flywheelTargetState = VaState(
            solution.speed, 0.0
        )
        Shooter.io.hoodTargetAngle = solution.phi
        Shooter.io.turretTargetState = PvState(
            Rotation2D(solution.theta), Rotation2D(0.0)
        )


        log("targetVelocity") value solution.speed
        log("launchAngle") value solution.phi

        log("FlywheelVelocityWithRBmotion") value Shooter.io.flywheelCurrentState.velocity
        log("MovingVertAngle") value Shooter.io.hoodTargetAngle

    }

    override fun end(interrupted: Boolean) {
        /**
         * Command flywheels to stop using feedback control.
         * Set flywheel power to 0 and hood angle to 0.
         */
        Shooter.io.flywheelUsingFeedback = false
        Shooter.io.turretUsingFeedback = false

        Shooter.io.flywheelSetPower(0.0)
    }

    override var name = { "ShootingState" }

    /**
     * @param angle CCW around Z axis. I.e +30deg means that audience side is
     * DOWN and -30deg means that audience side is UP.
     */
    fun getGoalPos(
        alliance: Globals.Alliance,
        angle: Rotation2D,
        side: Globals.Side
    ): Vector3D {
        val sideMul = if (side == Globals.Side.FAR_SIDE) 1 else -1
        val pivot = Vector3D(
            12.758 * if (alliance == Globals.Alliance.BLUE) 1 else -1,
            0,
            43.95
        )
        val pivotArm = Vector2D(21.45 * sideMul, -1.71) rotatedBy angle


        val faceVector = Vector2D(
            -ShootingConf.targetDepth * sideMul,
            ShootingConf.targetHeight
        ) rotatedBy angle

        // cell target is in the yz plane
        val cellTarget = pivotArm + faceVector
        val cellTarget3D = Vector3D(0, cellTarget.x, cellTarget.y)

        return pivot + cellTarget3D

    }

    data class ShotSolution(
        val time: Double,    // t, seconds
        val speed: Double,   // v, ball launch speed (m/s, or in/s if you switch units)
        val phi: Double,     // radians, measured from VERTICAL
        val theta: Double    // radians, field-frame heading from shooter to goal
    )

    fun solveShot(
        pos: Vector2D,
        vel: Vector2D,
        targetSlope: Double,
        shotTime: Double
    ): ShotSolution? {
        val hiveAngle = (
                if (pos.y < 0) Rotation2D(degrees(-30))
                else Rotation2D(degrees(30))
                )
        val r = 2.8 / 2 + ShootingConf.flywheelRadius / 2

        val goal = getGoalPos(
            Globals.alliance,
            hiveAngle,
            if (pos.y < 0) Globals.Side.AUDIENCE_SIDE
            else Globals.Side.FAR_SIDE
        ) + (vel * shotTime).threeD

        val dist = goal.groundPlane - pos
        val K = dist.mag + r * sin(ShootingConf.flywheelEntrancePhi)

        val deltaZ = goal.z - ShootingConf.flywheelOffset.z
        val lambda = hypot(K, 2 * deltaZ + targetSlope * K) / hypot(K, deltaZ)

        // ell = v * t, the gravity-free path length to the aim point
        val R = hypot(K, deltaZ)
        val ell = lambda * R

        val b = K / (ell + r)                 // sin(phi)
        if (abs(b) >= 1.0) return null        // cannot happen for lambda > 1
        val a = sqrt(1.0 - b * b)             // cos(phi), upward-launch branch

        val tSquared = (ell * a + r * b - deltaZ) / (386.09 / 2)
        if (tSquared <= 0.0) return null      // cannot happen for lambda > 1

        val t = sqrt(tSquared)
        val v = ell / t                               // v = ell / t
        val phi = atan2(b, a)                         // phi = atan2(sin, cos)
        val theta = atan2(dist.y, dist.x)             // direction of D

        return ShotSolution(t, v, phi, theta)
    }
    fun getShotTime(
        pos: Vector2D,
        vel: Vector2D,
        targetSlope: Double,
        shotTime: Double
    ): Double? = solveShot(pos, vel, targetSlope, shotTime)?.time
}