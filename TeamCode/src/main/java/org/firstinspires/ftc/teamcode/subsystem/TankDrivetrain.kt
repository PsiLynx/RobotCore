package org.firstinspires.ftc.teamcode.subsystem

import android.health.connect.datatypes.units.Power
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain.cornerPos
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain.headingController
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.abs
import kotlin.math.sign

object TankDrivetrain : Subsystem<TankDrivetrain>() {
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
        set(value) = pinpoint.setStart(value)
    val velocity: Pose2D
        get() = pinpoint.velocity

    val robotCentricVelocity: Pose2D
        get() = velocity rotatedBy -position.heading

    var gvfPaths = arrayListOf<Path>()
    private var poseHistory = Array(1000) { Pose2D() }

    init {
        motors.forEach {
            it.useInternalEncoder(384.5, millimeters(104))
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    fun resetPoseHistory() {
        poseHistory = Array(1000) { Pose2D() }
    }

    override fun update(deltaTime: Double) {
        log("position") value position.asAkitPose()

    }
    fun resetToCorner(next: Command) = (
        InstantCommand {
            pinpoint.hardwareDevice.resetPosAndIMU()
            position = cornerPos
        }
        andThen WaitCommand(0.5)
        andThen InstantCommand { next.schedule() }
    )

    fun driveFieldCentric(
        power: Pose2D,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ){
        val pose = power.vector.rotatedBy( -position.heading ) + power.heading
        setWeightedDrivePower(
            drive = pose.x,
            turn = pose.heading.toDouble(),
            feedForward = feedForward,
            comp = comp
        )
    }
    fun fieldCentricPowers(
        powers: List<Pose2D>,
        feedForward: Double,
        comp: Boolean
    ){
        var current = Pose2D()
        for(element in powers){
            var power = element
            power = ( power rotatedBy -position.heading )
            val next = current + power
            val maxPower = (
                  abs(next.x)
                + abs(next.y)
                + abs(next.heading.toDouble())
            )

            if (maxPower > 1) {
                // Compute scale factor to normalize max wheel power to 1
                val scale = (
                    ( 1 - (
                          abs(current.x)
                        + abs(current.y)
                        + abs(current.heading.toDouble())
                    ) ) / (
                          abs(power.x)
                        + abs(power.y)
                        + abs(power.heading.toDouble())
                    )
                )

                if (scale > 0) {
                    current += Pose2D(
                        power.x * scale,
                        power.y * scale,
                        power.heading.toDouble() * scale
                    )
                }
                break
            } else {
                current = next
            }
        }
        setWeightedDrivePower(
            current.x,
            current.heading.toDouble(),
            feedForward,
            comp
        )
    }
    fun resetHeading() {
        pinpoint.resetHeading()
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
        } else {
            frontLeft .power = leftPower
            frontRight.power = rightPower
        }
    }

    fun setWeightedDrivePower(
        drive: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ) {
        var leftPower  = drive - turn
        var rightPower = drive + turn

        differentialPowers(leftPower, rightPower, feedForward, comp)
    }
}
