package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.DrivetrainConf.HEADING_D
import org.firstinspires.ftc.teamcode.subsystem.DrivetrainConf.HEADING_P
import org.firstinspires.ftc.teamcode.util.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
import org.firstinspires.ftc.teamcode.util.Drawing
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.control.PIDFController
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

@Config
object DrivetrainConf{
    @JvmField var HEADING_P = 0.4
    @JvmField var HEADING_D = 0.6
}

object Drivetrain : Subsystem<Drivetrain>() {
    val pinpointPriority = 10.0

    private val frontLeft  = HardwareMap.frontLeft (FORWARD, 1.0, 1.0)
    private val frontRight = HardwareMap.frontRight(REVERSE, 1.0, 1.0)
    private val backLeft   = HardwareMap.backLeft  (FORWARD, 1.0, 1.0)
    private val backRight  = HardwareMap.backRight (REVERSE, 1.0, 1.0)
    val cornerPos = Pose2D(63, -66, PI / 2)
    var pinpointSetup = false

//    val octoQuad = OctoQuad(
//        "octoquad",
//        xPort = 0,
//        yPort = 1,
//        ticksPerMM = 13.26291192,
//        offset = Vector2D(-36.0 , -70.0),
//        xDirection = FORWARD,
//        yDirection = REVERSE,
//        headingScalar = 1.0
//    )
    val pinpoint = HardwareMap.pinpoint(pinpointPriority)
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

    override fun enable()  { pinpoint.priority = pinpointPriority }
    override fun disable() { pinpoint.priority = 0.0              }

    override fun update(deltaTime: Double) {
        controllers.forEach { it.updateError(deltaTime) }

        for(i in 1..<poseHistory.size){
            poseHistory[i - 1] = poseHistory[i]
        }
        poseHistory[poseHistory.lastIndex] = position

        gvfPaths.forEach { path -> Drawing.drawGVFPath(path, false) }

        Drawing.drawPoseHistory(poseHistory, "blue")
        Drawing.drawRobot(
            Pose2D(
                position.x,
                position.y,
                position.heading.toDouble() - PI / 2
            ),
            "blue"
        )
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
            strafe = -pose.y,
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
                + feedForward
            )

            if (maxPower > 1) {
                // Compute scale factor to normalize max wheel power to 1
                val scale = (
//                    (1 - feedForward)
//                    / (
//                          abs(power.x)
//                        + abs(power.y)
//                        + abs(power.heading.toDouble())
//                    )
                    1
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
            -current.y,
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
        controllers.forEach { it.resetController() }

        ensurePinpointSetup()
    }

    fun ensurePinpointSetup() {
        if(!pinpointSetup) {
            pinpoint.apply {
                xEncoderOffset = -36.0 // mm
                yEncoderOffset = -70.0 // mm
                podType = goBILDA_SWINGARM_POD
                xEncoderDirection = FORWARD
                yEncoderDirection = REVERSE
            }
            pinpointSetup = true
        }
    }

    fun setWeightedDrivePower(
        drive: Double = 0.0,
        strafe: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ) {
        var flPower = drive + strafe * 1.1 - turn
        var frPower = drive - strafe * 1.1 + turn
        var brPower = drive + strafe * 1.1 + turn
        var blPower = drive - strafe * 1.1 - turn
        flPower += feedForward * flPower.sign
        frPower += feedForward * frPower.sign
        brPower += feedForward * brPower.sign
        blPower += feedForward * blPower.sign
        val max = maxOf(flPower, frPower, brPower, blPower)
        if (max > 1 + 1e-4) {

            flPower /= max
            frPower /= max
            blPower /= max
            brPower /= max
        }
        if(comp){
            frontLeft .compPower( flPower )
            frontRight.compPower( frPower )
            backLeft  .compPower( blPower )
            backRight .compPower( brPower )
        } else {
            frontLeft .power = flPower
            frontRight.power = frPower
            backRight .power = brPower
            backLeft  .power = blPower
        }
    }

    val xVelocityController = PIDFController(
        P = 0.005,
        D = 0.0,
        setpointError = { - robotCentricVelocity.x },
        apply = { },
        pos = { 0.0 }
    )
    val yVelocityController = PIDFController(
        P = 0.005,
        D = 0.0,
        setpointError = { robotCentricVelocity.y },
        apply = { },
        pos = { 0.0 }
    )
    val headingController = PIDFController(
        P = { HEADING_P },
        D = { HEADING_D },
        setpointError = {
            arrayListOf(
                targetPosition - position.heading.toDouble(),
                targetPosition - position.heading.toDouble() + 2*PI,
                targetPosition - position.heading.toDouble() - 2*PI,
            ).minBy { abs(it) } // smallest absolute value with wraparound
        },
        apply = { },
        pos = { 0.0 }
    )
    private val controllers = arrayListOf(
        xVelocityController,
        yVelocityController,
        headingController
    )
}
