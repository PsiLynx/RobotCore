package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.controller.pid.TunablePIDF
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.DrivetrainConf.HEADING_D
import org.firstinspires.ftc.teamcode.subsystem.DrivetrainConf.HEADING_P
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

@Config
object DrivetrainConf{
    @JvmField var HEADING_P = 1.0
    @JvmField var HEADING_D = 1.0
}

object Drivetrain : Subsystem<Drivetrain>(), Tunable<Vector2D> {

    val shootingTargetHead get() : Double{
        return if(CommandScheduler.commands.firstOrNull{it is ShootingStateOTM} != null) Turret.fieldCentricAngle
        else (Globals.goalPose.groundPlane - position.vector).theta.toDouble()
    }
    val readyToShoot get() = (
        abs(
              ( position.heading.toDouble()   + 6*PI ) % ( 2* PI )
            - ( shootingTargetHead.toDouble() + 6*PI ) % ( 2* PI )
        ) / (2 * PI) < 0.01
    )


    override val tuningForward = Vector2D(10, 10)
    override val tuningBack = Vector2D(0, 0)
    override val tuningCommand = { it: State<*> ->
        followPath {
            start(position.vector)
            lineTo(it as Vector2D, forward)
        }
    }

    private val frontLeft  = HardwareMap.frontLeft (FORWARD)
    private val frontRight = HardwareMap.frontRight(REVERSE)
    private val backLeft   = HardwareMap.backLeft  (FORWARD)
    private val backRight  = HardwareMap.backRight (REVERSE)
    val cornerPos = Pose2D(63, -66, PI / 2)
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

    val robotCentricVelocity: Pose2D
        get() = velocity rotatedBy -position.heading

    var gvfPaths = arrayListOf<Path>()
    private var poseHistory = Array(1000) { Pose2D() }

    init {
        motors.forEach {
            //it.useInternalEncoder(384.5, millimeters(104))
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    fun resetPoseHistory() {
        poseHistory = Array(1000) { Pose2D() }
    }

    val tagReadGood get() = (
        Cameras.pose != Pose2D()
        && velocity.mag < 1
        && velocity.heading < 0.1
        && ( Globals.currentTime - Cameras.updateTime ) < 0.2
    )

    fun readAprilTags() = RunCommand {
        if(tagReadGood){
            position = Cameras.pose
            Robot.readingTag = true
        }
        else Robot.readingTag = false

    } withEnd { Robot.readingTag = false }

    fun headingLock(theta: Double) = run {
        it.headingController.targetPosition = theta

        it.setWeightedDrivePower(
            0.0, 0.0,
            it.headingController.feedback,
            0.03, true
        )
    } withEnd { setWeightedDrivePower() }

    override fun update(deltaTime: Double) {
        controllers.forEach { it.updateError(deltaTime) }

        log("position") value position

        log("heading controller") value headingController
        log("xVelocityController") value xVelocityController
        log("yVelocityController") value yVelocityController

        log("Ready to shoot") value readyToShoot

    }

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
            power = (
                ( power.vector rotatedBy -position.heading )
                + power.heading
            )
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
            -current.y,
            current.heading.toDouble(),
            feedForward,
            comp
        )
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
                xEncoderOffset = 120.65 // mm; hopefully this is accurate
                yEncoderOffset = 195.0 // mm -165.1 original
                podType = goBILDA_SWINGARM_POD
                xEncoderDirection = FORWARD
                yEncoderDirection = REVERSE
            }
            pinpointSetup = true
        }
    }

    fun power(drive: Double, strafe: Double, turn: Double) = run {
        setWeightedDrivePower(drive, strafe, turn)
    } withEnd {
        setWeightedDrivePower()
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
        if (max > 1) {

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

    @TunablePIDF(0.0, 1.0)
    val xVelocityController = PIDFController(
        P = 0.005,
        D = 0.0,
        setpointError = { - robotCentricVelocity.x },
        apply = { },
        pos = { 0.0 }
    )

    @TunablePIDF(0.0, 1.0)
    val yVelocityController = PIDFController(
        P = 0.005,
        D = 0.0,
        setpointError = { robotCentricVelocity.y },
        apply = { },
        pos = { 0.0 }
    )

    @TunablePIDF(0.0, PI / 2)
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
        pos = { position.heading.toDouble() }
    )
    private val controllers = arrayListOf<PIDFController>(
        xVelocityController,
        yVelocityController,
        headingController
    )
}
