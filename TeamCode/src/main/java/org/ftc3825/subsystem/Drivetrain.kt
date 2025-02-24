package org.ftc3825.subsystem

import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.component.Motor.ZeroPower.BRAKE
import org.ftc3825.gvf.GVFConstants
import org.ftc3825.gvf.GVFConstants.HEADING_D
import org.ftc3825.gvf.GVFConstants.HEADING_P
import org.ftc3825.gvf.Path
import org.ftc3825.util.GoBildaPinpointDriver
import org.ftc3825.util.Drawing
import org.ftc3825.util.Globals
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.pid.PidController
import org.ftc3825.util.pid.pdControl
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

object Drivetrain : Subsystem<Drivetrain> {
    private val frontLeft  = Motor(flMotorName, 312, FORWARD)
    private val frontRight = Motor(frMotorName, 312, REVERSE)
    private val backLeft   = Motor(blMotorName, 312, FORWARD)
    private val backRight  = Motor(brMotorName, 312, REVERSE)
    override var components = arrayListOf<Component>(frontLeft, backLeft, backRight, frontRight)
    val pinpoint = GlobalHardwareMap.get(
        GoBildaPinpointDriver::class.java, "odo"
    )

    private var startPos = Pose2D(0, 0, PI / 2)

    var position: Pose2D
        get() = (
            ( pinpoint.position rotatedBy startPos.heading )
            + startPos
        )
        set(value) {
            startPos = value - pinpoint.position
            poseHistory = Array(1000) { value }
        }
    val velocity: Pose2D
        get() = pinpoint.velocity rotatedBy startPos.heading

    val robotCentricVelocity: Pose2D
        get() = velocity rotatedBy -position.heading

    var gvfPaths = arrayListOf<Path>()
    private var poseHistory = Array(1000) { Pose2D() }

    var targetHeading = Rotation2D()
    var holdingHeading = false

    init {
        motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(BRAKE)
        }
    }

    override fun update(deltaTime: Double) {
        controllers.forEach { it.updateError(deltaTime) }
        pinpoint.update()
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

    fun driveFieldCentric(power: Pose2D, feedForward: Double = 0.0, comp: Boolean = false){
        val pose = power.vector.rotatedBy( -position.heading ) + power.heading
        setWeightedDrivePower(
            drive = pose.x,
            strafe = -pose.y,
            turn = pose.heading.toDouble(),
            feedForward = feedForward,
            comp = comp
        )
    }

    override fun reset() {
        super.reset()
        holdingHeading = false
        controllers.forEach { it.resetController() }
        //This uses mm, to use inches multiply by 25.4
        pinpoint.setOffsets(
            -36.0,
            -70.0
        )

        pinpoint.setEncoderResolution(
            GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
        )
        pinpoint.setEncoderDirections(
            GoBildaPinpointDriver.EncoderDirection.FORWARD,
            GoBildaPinpointDriver.EncoderDirection.REVERSED
        )
        targetHeading = position.heading
        startPos = Pose2D(0, 0, PI / 2)
    }
    fun setWeightedDrivePower(
        drive: Double = 0.0,
        strafe: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ) {
        var flPower = drive + strafe - turn
        var frPower = drive - strafe + turn
        var brPower = drive + strafe + turn
        var blPower = drive - strafe - turn
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

    val xVelocityController = PidController(
        P = 0.005,
        D = 0.0,
        setpointError = { - robotCentricVelocity.x },
        apply = { },
        pos = { 0.0 }
    )
    val yVelocityController = PidController(
        P = 0.005,
        D = 0.0,
        setpointError = { robotCentricVelocity.y },
        apply = { },
        pos = { 0.0 }
    )
    val headingVelocityController = PidController(
        P = 0.05,
        D = 0.0,
        setpointError = { - robotCentricVelocity.heading.toDouble() },
        apply = { },
        pos = { 0.0 }
    )
    val headingController = PidController(
        P = 1.0,
        D = 4.0,
        setpointError = {
            arrayListOf(
                (targetHeading - position.heading).toDouble(),
                (targetHeading - position.heading).toDouble() + 2*PI,
                (targetHeading - position.heading).toDouble() - 2*PI,
            ).minBy { abs(it) } // smallest absolute value with wraparound
        },
        apply = { },
        pos = { 0.0 }
    )
    private val controllers = arrayListOf(
        xVelocityController,
        yVelocityController,
        headingVelocityController,
        headingController
    )
}
