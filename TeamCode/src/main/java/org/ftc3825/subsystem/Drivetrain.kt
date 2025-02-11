package org.ftc3825.subsystem

import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.component.Motor.ZeroPower.BRAKE
import org.ftc3825.component.Motor.ZeroPower.FLOAT
import org.ftc3825.gvf.Path
import org.ftc3825.util.GoBildaPinpointDriver
import org.ftc3825.util.Drawing
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName
import org.ftc3825.util.geometry.DrivePowers
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.geometry.Vector2D
import org.ftc3825.util.pid.PidController
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

    private var startPos: Pose2D = Pose2D(0, 0, 0)

    var position: Pose2D
        get() = ( Pose2D(pinpoint.position) + startPos)
        set(value) {
            startPos = value - Pose2D(pinpoint.position)
        }
    val velocity: Pose2D
        get() = ( Pose2D(pinpoint.velocity) rotatedBy  startPos.heading ).apply {
        }

    val robotCentricVelocity: Pose2D
        get() = velocity rotatedBy -position.heading

    var gvfPaths = arrayListOf<Path>()
    var poseHistory = Array(1000) { Pose2D() }

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

        gvfPaths.forEach { Drawing.drawGVFPath(it, "#3F51B5") }

        Drawing.drawPoseHistory(poseHistory, "green")
        Drawing.drawRobot(
            Pose2D(
                position.x,
                position.y,
                position.heading.toDouble()
            ),
            "green"
        )
    }

    fun driveFieldCentric(power: Pose2D, feedForward: Double = 0.0){
        val pose = power.vector.rotatedBy( -position.heading ) + power.heading
        println("Drive: ${pose.x}, Strafe: ${-pose.y}")
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
        frontLeft.power  = powers[0]
        backLeft.power   = powers[1]
        frontRight.power = powers[2]
        backRight.power  = powers[3]
    }

    override fun reset() {
        super.reset()
        holdingHeading = false
        controllers.forEach { it.resetController() }
        //This uses mm, to use inches multiply by 25.4
        pinpoint.setOffsets(
            //- ( 1 + 9.0/16 ) * 25.4,
            //- ( 2 + 5.0/8  ) * 25.4
            0.0, 0.0
        )

        pinpoint.setEncoderResolution(
            GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
        )
        pinpoint.setEncoderDirections(
            GoBildaPinpointDriver.EncoderDirection.FORWARD,
            GoBildaPinpointDriver.EncoderDirection.FORWARD
        )
        targetHeading = position.heading
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
        frontLeft.power  = lfPower
        frontRight.power = rfPower
        backRight.power  = rbPower
        backLeft.power   = lbPower
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
