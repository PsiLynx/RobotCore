package org.ftc3825.subsystem

import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component
import org.ftc3825.component.Motor
import org.ftc3825.component.Motor.ZeroPower.FLOAT
import org.ftc3825.component.Motor.Direction.FORWARD
import org.ftc3825.component.Motor.Direction.REVERSE
import org.ftc3825.pedroPathing.follower.Follower
import org.ftc3825.pedroPathing.localization.Pose
import org.ftc3825.pedroPathing.pathGeneration.Path
import org.ftc3825.pedroPathing.pathGeneration.PathChain
import org.ftc3825.pedroPathing.util.Drawing
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.Vector2D
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName
import org.ftc3825.util.pid.PidController
import kotlin.math.PI
import kotlin.math.abs

object Drivetrain : Subsystem<Drivetrain> {
    private val frontLeft  = Motor(flMotorName, 312, REVERSE)
    private val frontRight = Motor(frMotorName, 312, FORWARD)
    private val backLeft   = Motor(blMotorName, 312, REVERSE)
    private val backRight  = Motor(brMotorName, 312, FORWARD)
    private val follower = Follower(GlobalHardwareMap.hardwareMap)
    override var components = arrayListOf<Component>(frontLeft, backLeft, backRight, frontRight)

    var allPaths = arrayListOf<PathChain>()

    var targetHeading = Rotation2D()
    var holdingHeading = false

    private val xVelocityController = PidController(
        P = 0.01,
        D = 0.05,
        setpointError = { - robotCentricVelocity.x },
        apply = { }
    )
    private val yVelocityController = PidController(
        P = 0.01,
        D = 0.05,
        setpointError = { robotCentricVelocity.y },
        apply = { }
    )
    private val headingVelocityController = PidController(
        P = 0.1,
        D = 0.2,
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

    val isFollowing: Boolean
        get() = follower.isBusy
    val currentPath: Path?
        get() = follower.currentPath

    val robotCentricVelocity: Pose2D
        get() = (
            ( Vector2D(follower.velocity!!) rotatedBy -position.heading )
            + Rotation2D(follower.poseUpdater.angularVelocity)
        )

    val velocity: Pose2D
        get() = (
            Vector2D(follower.velocity!!)
                + Rotation2D(follower.poseUpdater.angularVelocity)
        )

    var position: Pose2D
        get() = Pose2D(follower.pose)
        set(value) = follower.setStartingPose(Pose(
            value.x, value.y, value.heading.toDouble()
        ))

    init {
        motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    override fun update(deltaTime: Double) {
        controllers.forEach { it.updateError(deltaTime) }

        if(follower.currentPath != null){
            follower.update()
            allPaths.forEach { Drawing.drawPath(it, "#3F51B5")}
            Drawing.drawDebug(follower)
        }
        else follower.poseUpdater.update()
    }

    fun driveFieldCentric(power: Pose2D) = setWeightedDrivePower(
        power.vector.rotatedBy(position.heading) + power.heading
    )

    fun setTeleopPowers(drive: Double, strafe: Double, turn: Double){
        val translational = if(drive == 0.0 && strafe == 0.0){
            //println("using translational velocity control")
            Vector2D(xVelocityController.feedback, yVelocityController.feedback)
        } else ( Vector2D(drive, strafe) rotatedBy position.heading )

        if(abs(robotCentricVelocity.heading.toDouble()) < 0.1) holdingHeading = true

        val rotational = if(turn == 0.0 && !holdingHeading){
            //println("using rotational velocity control")
            targetHeading = position.heading
            Rotation2D(headingVelocityController.feedback)
        } else if(turn == 0.0) {
            //println("holding heading")
            Rotation2D(headingController.feedback)
        } else{
            holdingHeading = false
            Rotation2D(turn)
        }

        setWeightedDrivePower(translational + rotational)
    }

    fun setWeightedDrivePower(power: Pose2D) =
        setWeightedDrivePower(power.x, power.y, power.heading.toDouble())

    fun setWeightedDrivePower(drive: Double, strafe: Double, turn: Double) {
        var lfPower = drive + strafe - turn
        var rfPower = drive - strafe + turn
        var rbPower = drive + strafe + turn
        var lbPower = drive - strafe - turn
        val max = maxOf(lfPower, rfPower, rbPower, lbPower)
        if (max > 1) {
            lfPower /= max
            rfPower /= max
            rbPower /= max
            lbPower /= max
        }
        frontLeft.setPower(lfPower)
        frontRight.setPower(rfPower)
        backRight.setPower(rbPower)
        backLeft.setPower(lbPower)
    }

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
    }

    fun setMaxFollowerPower(power: Double) = follower.setMaxPower(power)
    fun followPath(path: PathChain)        = follower.followPath(path)
    fun breakFollowing()                   = follower.breakFollowing()
}
