package org.firstinspires.ftc.teamcode.subsystem.drivetrain

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.PI

class RealIo: Io, RealSubsystemIo() {


    override fun update(deltaTime: Double) {
        acceleration = (velocity - lastVelocity) / deltaTime
        lastVelocity = velocity
    }

    private val frontLeft  = HardwareMap.frontLeft (FORWARD)
    private val backLeft   = HardwareMap.backLeft  (FORWARD)
    private val frontRight = HardwareMap.frontRight(REVERSE)
    private val backRight  = HardwareMap.backRight (REVERSE)

    private val octoQuad = HardwareMap.octoQuad(
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

    override val components = listOf(
        frontRight,
        frontLeft,
        backLeft,
        backRight,
        octoQuad
    )

    init {
        motors.forEach {
            it.useInternalEncoder(384.5, millimeters(104))
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    override var position: Pose2D
        get() = octoQuad.position.vector + octoQuad.position.heading % (2*PI)
        set(value) = octoQuad.setPos(value)
    override val velocity: Pose2D
        get() = octoQuad.velocity
    override var acceleration: Pose2D = Pose2D()
        private set
    private var lastVelocity = velocity

    override fun drivePowers(speeds: ChassisSpeeds) {
        powers = speeds
        with(speeds) {
            frontLeft.power = vy + vx + vTheta
            backLeft.power = vy - vx + vTheta
            frontRight.power = vy - vx - vTheta
            backRight.power = vy + vx - vTheta
        }
    }

    override var powers = ChassisSpeeds()
        private set

    override fun resetLocalizer() {
        octoQuad.reset()
    }

}