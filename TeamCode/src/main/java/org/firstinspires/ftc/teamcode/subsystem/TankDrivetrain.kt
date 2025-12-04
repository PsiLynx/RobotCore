package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain.headingController
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

object TankDrivetrain : Subsystem<TankDrivetrain>() {
    const val MAX_VELO = 75.0
    const val MAX_HEADING_VELO = 3.5 * PI

    private val frontLeft  = HardwareMap.frontLeft (FORWARD)
    private val frontRight = HardwareMap.frontRight(REVERSE)
    private val backLeft   = HardwareMap.backLeft  (FORWARD)
    private val backRight  = HardwareMap.backRight (REVERSE)
    var pinpointSetup = false

    val pinpoint = HardwareMap.pinpoint()
    override var components: List<Component> = arrayListOf<Component>(
        frontLeft,
        backLeft,
        backRight,
        frontRight,
        pinpoint
    )

    var position: Pose2D
        get() = pinpoint.position
        set(value) = pinpoint.setPos(value)

    val velocity: Pose2D
        get() = pinpoint.velocity

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

    override fun reset() {
        super.reset()
        pinpoint.resetInternals()
        headingController.targetPosition = position.heading.toDouble()

        ensurePinpointSetup()
    }

    fun ensurePinpointSetup() {
        if(!pinpointSetup) {
            pinpoint.apply {
                xEncoderOffset = 120.65 // mm; hopefully this is accurate
                yEncoderOffset = 195.0 // mm -165.1 original
                podType = goBILDA_SWINGARM_POD
                xEncoderDirection = FORWARD
                yEncoderDirection = REVERSE
            }
            pinpointSetup = true
        }
    }

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
            frontRight.compPower( rightPower )
            backLeft .compPower( leftPower )
            backRight.compPower( rightPower )
        } else {
            frontLeft .power = leftPower
            frontRight.power = rightPower
            backLeft .power = leftPower
            backRight.power = rightPower
        }
    }

    fun setWeightedDrivePower(
        drive: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ){
        var _drive = drive
        var _turn = turn
        if(abs(drive) + abs(turn) + feedForward > 1){
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
