package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.shooter.CompTargets.compGoalPos
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.subsystem.TankDriveConf.P
import org.firstinspires.ftc.teamcode.subsystem.TankDriveConf.D
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Range
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sign
import kotlin.math.sin

@Config object TankDriveConf {
    @JvmField var P = 1.3
    @JvmField var D = 0.15
}

object TankDrivetrain : Subsystem<TankDrivetrain>() {
    const val MAX_VELO = 96.0
    const val MAX_HEADING_VELO = 4 * PI * 8.0/7

    private val frontLeft  = HardwareMap.frontLeft (FORWARD)
    private val backLeft   = HardwareMap.backLeft  (FORWARD)
    private val frontRight = HardwareMap.frontRight(REVERSE)
    private val backRight  = HardwareMap.backRight (REVERSE)

    var pwmBreakingState = 0
    private set

    val powers get() = ChassisSpeeds(
        0.0,
        ( frontRight.power + frontLeft.power ) / 2,
        ( frontRight.power - frontLeft.power ) / 2,
    )
    val octoQuad = HardwareMap.octoQuad(
        xPort = 0,
        yPort = 1,
        ticksPerMM = 2000 / (32 * PI),
        offset = Vector2D(
            x = -54.0,
            y = -82.0,
        ),
        xDirection = FORWARD,
        yDirection = FORWARD,
        headingScalar = 1.0127
    )
    override var components: List<Component> = arrayListOf<Component>(
        frontLeft,
        backLeft,
        backRight,
        frontRight,
        octoQuad
    )

    val shootingTargetHead get() = (
        compGoalPos().groundPlane - position.vector
    ).theta.toDouble() + PI
    var tagReadGood = false

    var position: Pose2D
        get() = octoQuad.position.vector + octoQuad.position.heading % (2*PI)
        set(value) = octoQuad.setPos(value)


    val velocity: Pose2D
        get() = octoQuad.velocity


    private var lastVelocity = Pose2D()

    var acceleration = Pose2D()
        internal set


    val forwardsVelocity: Double
        get() = velocity.vector.magInDirection(position.heading)

    val forwardsAcceleration: Double
        get() = acceleration.vector.magInDirection(position.heading)

    init {
        motors.forEach {
            it.useInternalEncoder(384.5, millimeters(104))
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    override fun update(deltaTime: Double) {
        acceleration = velocity - lastVelocity
        lastVelocity = velocity

        log("position") value position
        log("velocity") value velocity
        log("futurePose (0.1s)") value futurePos(0.1)
        log("robotCentricVelocity") value ChassisSpeeds(
            0.0,
            forwardsVelocity,
            velocity.heading.toDouble(),
        )
        log("acceleration") value acceleration
        log("forwardsVelocity") value forwardsVelocity

    }

    fun readAprilTags() = RunCommand {
        if(tagReadGood){
            position = Cameras.pose
            Robot.readingTag = true
        }
        else Robot.readingTag = false

    } withEnd { Robot.readingTag = false } withName "Td: readAprilTags"

    fun headingLock(theta: Rotation2D) = (
        run {
            setWeightedDrivePower(
                turn = PvState(
                    (theta - position.heading).normalized(),
                    velocity.heading
                ).applyPD(P, D).toDouble()
            )
        }
        withEnd { setWeightedDrivePower() }
        withName "Td: lock(${floor(theta.toDouble() * 100) / 100})"
        withDescription { "" }
    )
    fun headingLock(theta: Double) = headingLock(Rotation2D(theta))

    fun inShootingZone(deadBand: Double = 4.0): Boolean {
        val robotVertices = arrayOf(
            position.vector + Vector2D(  4.5,  6),
            position.vector + Vector2D(  4.5, -6),
            position.vector + Vector2D( -4.5,  6),
            position.vector + Vector2D( -4.5, -6),
        ).map { it rotatedBy position.heading }

        val frontZoneVertices = arrayOf(
            Vector2D(-72, 72) - Vector2D(-1, 1)*deadBand,
            Vector2D(0, -deadBand),
            Vector2D(-72, 72) - Vector2D(-1, 1)*deadBand,
        )

        val axes = arrayOf(
            position.heading,
            position.heading + Rotation2D(PI/2),
            Rotation2D(-PI/4),
            Rotation2D(-3*PI/4),
        ).map { it * Vector2D(1, 0) }

        axes.forEach { axis ->
            if(
                Range(
                    robotVertices.minOf { it dot axis },
                    robotVertices.maxOf { it dot axis },
                ) overlaps Range(
                    frontZoneVertices.minOf { it dot axis },
                    frontZoneVertices.maxOf { it dot axis },
                )
            ) return true
        }
        return false
    }

    fun resetLocalizer() = octoQuad.resetInternals()

    fun differentialPowers(
        left: Double,
        right: Double,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ){
        var leftPower = left
        var rightPower = right

        leftPower  += feedForward * leftPower.sign
        rightPower += feedForward * rightPower.sign

        val max = maxOf(leftPower, rightPower)

        if (max > 1) {
            leftPower /= max
            rightPower /= max
        }

        if(comp){
            frontLeft .compPower( leftPower )
            backLeft .compPower( leftPower )
            frontRight.compPower( rightPower )
            backRight.compPower( rightPower )
        } else {
            frontLeft .power = leftPower
            backLeft .power = leftPower
            frontRight.power = rightPower
            backRight.power = rightPower
        }
    }

    fun power(
        drive: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ) = (
        run { setWeightedDrivePower(drive, turn, feedForward, comp) }
        withEnd { setWeightedDrivePower() }
    )

    /**
     * Computes the approximate future position of the drive base.
     * Warning: This method does not take into account
     * the current acceleration of the robot, thus the
     * farther out the calculation, the worse it will be.
     * @param dt The number of seconds in the future the estimation should
     * be made for.
     * @return The approximate position of the robot dt number of seconds
     * in the future.
     */

    fun futurePos(
        dt: Double,
        position: Pose2D = TankDrivetrain.position,
        velocity: Pose2D = TankDrivetrain.velocity,
    ): Pose2D {
        val omega = velocity.heading.toDouble()
        val speed = velocity.vector.mag
        val theta0 = position.heading.toDouble()
        val dTheta = omega * dt

        if (abs(omega) < 1e-3) {
            return position + velocity * dt
        }

        val R = speed / omega
        log("theta0") value theta0
        log("dTheta") value dTheta
        log("sum") value (theta0 + dTheta)

        return Pose2D(
            position.x + R * (sin(theta0 + dTheta) - sin(theta0)),
            position.y - R * (cos(theta0 + dTheta) - cos(theta0)),
            theta0 + dTheta
        )
    }

    fun setWeightedDrivePower(
        drive: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false,
        slew: Boolean = false
    ){
        var _drive = drive
        var _turn = turn
        log("drive in") value _drive * MAX_VELO

        if(
            slew
            && abs(_drive) < 0.1
        ){
             when(pwmBreakingState){
                0 -> {
                    pwmBreakingState = 1
                    _drive = (
                        if(abs(forwardsVelocity) > 40)
                            0.01 * forwardsVelocity.sign

                        else 0.0
                    )
                }
                1 -> {
                    pwmBreakingState = 2
                    _drive = (
                        if(abs(forwardsVelocity) > 70)
                            0.01 * forwardsVelocity.sign

                        else 0.0
                    )
                }
                2 -> {
                    pwmBreakingState = 0
                    _drive = 0.0
                }
            }
        }
        log("slew") value (
            abs( _drive - powers.vy ) / CommandScheduler.deltaTime
        )

        if(abs(_drive) + abs(_turn) + feedForward > 1){
            val drive_max = ( 1 - feedForward - abs(_turn) ).coerceIn(0.0, 1.0)
            if(abs(_drive) > drive_max){
                _drive = drive_max * _drive.sign
            }
        }


        differentialPowers(
            _drive - _turn,
            _drive + _turn,
            feedForward,
            comp
        )

        log("drive") value _drive
        log("turn") value _turn
    }
}
