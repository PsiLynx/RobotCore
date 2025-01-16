package org.ftc3825.subsystem

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.component.Motor.ZeroPower.FLOAT
import org.ftc3825.pedroPathing.follower.Follower
import org.ftc3825.pedroPathing.localization.GoBildaPinpointDriver
import org.ftc3825.pedroPathing.localization.Pose
import org.ftc3825.pedroPathing.pathGeneration.Path
import org.ftc3825.pedroPathing.pathGeneration.PathChain
import org.ftc3825.pedroPathing.util.Drawing
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName
import org.ftc3825.util.geometry.DrivePowers
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.pid.PidController
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

object Drivetrain : Subsystem<Drivetrain> {
    private val frontLeft  = Motor(flMotorName, 312, FORWARD)
    private val frontRight = Motor(frMotorName, 312, REVERSE)
    private val backLeft   = Motor(blMotorName, 312, FORWARD)
    private val backRight  = Motor(brMotorName, 312, REVERSE)
    private val follower = Follower(GlobalHardwareMap.hardwareMap)
    override var components = arrayListOf<Component>(frontLeft, backLeft, backRight, frontRight)
    private val pinpoint = GlobalHardwareMap.get(
        GoBildaPinpointDriver::class.java, "odo"
    )

    private var startPos: Pose2D = Pose2D(0, 0, 0)

    var position: Pose2D
        get() = (
            Pose2D(pinpoint.position)
            rotatedBy startPos.heading
        ) + startPos
        set(value) {
            startPos = value - position
        }
    val velocity: Pose2D
        get() = ( Pose2D(pinpoint.velocity) rotatedBy  startPos.heading )

    val robotCentricVelocity: Pose2D
        get() = velocity rotatedBy -position.heading

    var allPaths = arrayListOf<PathChain>()
    var gvfPaths = arrayListOf<org.ftc3825.gvf.Path>()
    var poseHistory = arrayListOf<Pose2D>()

    var targetHeading = Rotation2D()
    var holdingHeading = false

    val isFollowing: Boolean
        get() = follower.isBusy
    val currentPath: Path?
        get() = follower.currentPath

    init {
        motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    override fun update(deltaTime: Double) {
        controllers.forEach { it.updateError(deltaTime) }
        pinpoint.update()
        poseHistory.add(position)
        poseHistory.removeAt(0)

        if(follower.currentPath != null){
            follower.update()
            Drawing.drawDebug(follower)
            allPaths.forEach { Drawing.drawPath(it, "#3F51B5")}
        }
        else follower.poseUpdater.update()

        gvfPaths.forEach { Drawing.drawGVFPath(it, "#3F51B5") }

        Drawing.drawPoseHistory(poseHistory, "green")
        Drawing.drawRobot(
            Pose(
                position.x,
                position.y,
                position.heading.toDouble()
            ),
            "green"
        )
    }

    fun driveFieldCentric(power: Pose2D, feedForward: Double = 0.0){
        val pose = power.vector.rotatedBy( -position.heading ) + power.heading
        setWeightedDrivePower(
            DrivePowers(
                drive = pose.x,
                strafe = -pose.y,
                turn = pose.heading.toDouble(),
            ),
            feedForward = feedForward
        )
    }

    fun setWeightedDrivePower(power: DrivePowers, feedForward: Double = 0.0) =
        setWeightedDrivePower(
            power.drive,
            power.strafe,
            power.turn,
            feedForward
        )

    fun setMotorPowers(
        leftFront: Double,
        leftRear: Double,
        rightFront: Double,
        rightRear: Double
    ) = setMotorPowers(
        arrayListOf(leftFront, leftRear, rightFront, rightRear).toDoubleArray()
    )

    fun setMotorPowers(powers: DoubleArray){
        frontLeft.setPower(powers[0])
        backLeft.setPower(powers[1])
        frontRight.setPower(powers[2])
        backRight.setPower(powers[3])
    }

    override fun reset() {
        super.reset()
        targetHeading = position.heading
        holdingHeading = false
        controllers.forEach { it.resetController() }
        pinpoint.resetPosAndIMU()
        //This uses mm, to use inches divide these numbers by 25.4
        pinpoint.setOffsets(173.04, -133.35)

        pinpoint.setEncoderResolution(
            GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
        )
        pinpoint.setEncoderDirections(
            GoBildaPinpointDriver.EncoderDirection.REVERSED,
            GoBildaPinpointDriver.EncoderDirection.FORWARD
        )
    }
    fun setWeightedDrivePower(
        drive: Double,
        strafe: Double,
        turn: Double,
        feedForward: Double = 0.0
    ) {
        var lfPower = drive + strafe - turn
        var rfPower = drive - strafe + turn
        var rbPower = drive + strafe + turn
        var lbPower = drive - strafe - turn
        lfPower += feedForward * lfPower.sign
        rfPower += feedForward * rfPower.sign
        rbPower += feedForward * rbPower.sign
        lbPower += feedForward * lbPower.sign
        val max = maxOf(lfPower, rfPower, rbPower, lbPower)
        if (max > 1) {
            lfPower /= max
            rfPower /= max
            rbPower /= max
            lbPower /= max
        }
        frontLeft.setPower ( lfPower )
        frontRight.setPower( rfPower )
        backRight.setPower ( rbPower )
        backLeft.setPower  ( lbPower )
    }

    fun setMaxFollowerPower(power: Double) = follower.setMaxPower(power)
    fun followPath(path: PathChain)        = follower.followPath(path)
    fun breakFollowing()                   = follower.breakFollowing()

    val xVelocityController = PidController(
        P = 0.005,
        D = 0.0,
        setpointError = { - robotCentricVelocity.x },
        apply = { }
    )
    val yVelocityController = PidController(
        P = 0.005,
        D = 0.0,
        setpointError = { robotCentricVelocity.y },
        apply = { }
    )
    val headingVelocityController = PidController(
        P = 0.05,
        D = 0.0,
        setpointError = { - robotCentricVelocity.heading.toDouble() },
        apply = { }
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
        apply = { }
    )
    private val controllers = arrayListOf(
        xVelocityController,
        yVelocityController,
        headingVelocityController,
        headingController
    )
}
