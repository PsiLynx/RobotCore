package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
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
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

@Config object TankDriveConf {
    @JvmField var P = 2.3
    @JvmField var D = 0.1
}
object TankDrivetrain : Subsystem<TankDrivetrain>() {
    const val MAX_VELO = 80.0
    const val MAX_HEADING_VELO = 4 * PI * 8.0/7

    private val frontLeft  = HardwareMap.frontLeft (FORWARD)
    private val backLeft   = HardwareMap.backLeft  (FORWARD)
    private val frontRight = HardwareMap.frontRight(REVERSE)
    private val backRight  = HardwareMap.backRight (REVERSE)

    val octoQuad = HardwareMap.octoQuad(
        xPort = 0,
        yPort = 1,
        ticksPerMM = 2000 / (32 * PI),
        offset = Vector2D(
            x = -43.0,
            y = -50.0,
        ),
        xDirection = FORWARD,
        yDirection = FORWARD,
        headingScalar = 1.0
    )
    override var components: List<Component> = arrayListOf<Component>(
        frontLeft,
        backLeft,
        backRight,
        frontRight,
        octoQuad
    )

    val shootingTargetHead get() = (
        Globals.goalPose.groundPlane - position.vector
    ).theta.toDouble() + PI
    var tagReadGood = false

    var position: Pose2D
        get() = octoQuad.position
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
        log("position") value position
        log("velocity") value velocity
        log("robotCentricVelocity") value ChassisSpeeds(
            0.0,
            forwardsVelocity,
            velocity.heading.toDouble(),
        )
        log("acceleration") value acceleration
        log("forwardsVelocity") value forwardsVelocity
        acceleration = velocity - lastVelocity
        lastVelocity = velocity

    }

    fun readAprilTags() = RunCommand {
        if(tagReadGood){
            position = Cameras.pose
            Robot.readingTag = true
        }
        else Robot.readingTag = false

    } withEnd { Robot.readingTag = false } withName "Td: readAprilTags"

    fun headingLock(theta: Double) = run {
        setWeightedDrivePower(
            turn = PvState(
                Rotation2D(theta) - position.heading,
                velocity.heading
            ).applyPD(P, D).toDouble()
        )
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
     * @param dt The number of seconds the estimation should
     * be made for.
     * @return The approximate position of the robot dt number of seconds
     * in the future.
     */

    fun futurePos(dt: Double): Pose2D {
        return Pose2D(position.vector + velocity.vector * dt, position.heading + velocity.heading * dt)
    }

    fun setWeightedDrivePower(
        drive: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ){
        var _drive = drive
        var _turn = turn
//        if(abs(drive) + abs(turn) + feedForward > 1){
//            val drive_max = ( 1 - feedForward - abs(_turn) ).coerceIn(0.0, 1.0)
//            if(abs(_drive) > drive_max){
//                _drive = drive_max * _drive.sign
//            }
//        }
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
