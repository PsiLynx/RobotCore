package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.ThreeDeadWheelLocalizer
import org.firstinspires.ftc.teamcode.component.IMU
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Rotation2D
import kotlin.math.max

class Drivetrain(hardwareMap: HardwareMap) : Subsystem(hardwareMap) {
    private val frontLeft: Motor = Motor("frontLeft", hardwareMap, 312)
    private val frontRight: Motor = Motor("frontRight", hardwareMap, 312)
    private val backRight: Motor = Motor("backRight", hardwareMap, 312)
    private val backLeft: Motor = Motor("backLeft", hardwareMap, 312)
    private val imu = IMU("imu", hardwareMap)
    private val localizer = ThreeDeadWheelLocalizer(
        frontLeft.motor,
        backRight.motor,
        frontRight.motor
    )

    var poseEstimate = Pose2D(0.0, 0.0, 0.0)

    init {
        frontLeft.setDirection(Motor.FORWARD)
        frontRight.setDirection(Motor.REVERSE)
        backLeft.setDirection(Motor.FORWARD)
        backRight.setDirection(Motor.REVERSE)
        imu
        localizer
    }

    fun setWeightedDrivePower(power: Pose2D) {
        val drive = power.x
        val strafe = power.y
        val turn = power.heading
        setWeightedDrivePower(drive, strafe, turn)
    }

    fun setWeightedDrivePower(drive: Double, strafe: Double, turn: Double) {
        var lfPower = drive + strafe + turn
        var rfPower = drive - strafe - turn
        var rbPower = drive + strafe - turn
        var lbPower = drive - strafe + turn
        val max = max(
                max(lfPower, rfPower),
                max(rbPower, lbPower)
        )
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

    fun setIMUYaw(angle: Double) {
        imu.yaw = Rotation2D(angle)
    }
    fun setIMUYaw(angle: Rotation2D){
        imu.yaw = angle
    }

    fun resetIMUYaw() {
        imu.yaw = Rotation2D()
    }

    fun driveFieldCentric(power: Pose2D) {
        var rotatedPowers = power - imu.yaw
        setWeightedDrivePower(power)
    }

    companion object{
        private const val ticksPerRev = 1.0
        private const val wheelRadius = 1.0
        private const val inchesPerTick = ticksPerRev * 2 * Math.PI * wheelRadius
    }
}
