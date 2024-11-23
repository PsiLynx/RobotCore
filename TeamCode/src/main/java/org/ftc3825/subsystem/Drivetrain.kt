package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component
import org.ftc3825.component.Motor
import org.ftc3825.component.Motor.Direction.FORWARD
import org.ftc3825.component.Motor.Direction.REVERSE
import org.ftc3825.pedroPathing.follower.Follower
import org.ftc3825.pedroPathing.localization.Pose
import org.ftc3825.pedroPathing.pathGeneration.PathChain
import org.ftc3825.pedroPathing.util.Drawing
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName

object Drivetrain : Subsystem<Drivetrain> {
    private val frontLeft  = Motor(flMotorName, 312, REVERSE)
    private val frontRight = Motor(frMotorName, 312, FORWARD)
    private val backLeft   = Motor(blMotorName, 312, REVERSE)
    private val backRight  = Motor(brMotorName, 312, FORWARD)

    override var components = arrayListOf<Component>(frontLeft, backLeft, backRight, frontRight)

    private val follower = Follower(GlobalHardwareMap.hardwareMap)

    var pos: Pose2D
        get() = Pose2D(follower.pose.x, follower.pose.y, follower.pose.heading)
        set(value) = follower.setStartingPose(Pose(
            value.x,
            value.y,
            value.heading.theta
        ))

    init {
        motors.forEach {
            it.useInternalEncoder()
            it.hardwareDevice.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        }
    }

    override fun update(deltaTime: Double) {
        components.forEach { it.update(deltaTime) }

        if(follower.currentPath != null) {
            follower.update()
            Drawing.drawDebug(follower)
        }
        else {
            follower.poseUpdater.update()
        }
    }

    fun driveFieldCentric(power: Pose2D) = setWeightedDrivePower(
        power.vector.rotatedBy(pos.heading) + power.heading
    )

    fun setWeightedDrivePower(power: Pose2D) = setWeightedDrivePower(power.x, power.y, power.heading.theta)

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
    ) = setMotorPowers(arrayListOf(leftFront, leftRear, rightFront, rightRear).toDoubleArray())

    fun setMotorPowers( powers: DoubleArray ){
        frontLeft.setPower(powers[0])
        backLeft.setPower(powers[1])
        frontRight.setPower(powers[2])
        backRight.setPower(powers[3])
    }

    fun setMaxFollowerPower(power: Double) = follower.setMaxPower(power)
    fun followPath(path: PathChain)        = follower.followPath(path)
    fun breakFollowing()                   = follower.breakFollowing()
}
