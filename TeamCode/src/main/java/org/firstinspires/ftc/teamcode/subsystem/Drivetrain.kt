package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.roadrunner.Pose2d
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer
import org.firstinspires.ftc.teamcode.Vector2D
import org.firstinspires.ftc.teamcode.component.IMU
import org.firstinspires.ftc.teamcode.component.Motor

class Drivetrain(hardwareMap: HardwareMap) : Subsystem(hardwareMap) {
    private val frontLeft: Motor
    private val frontRight: Motor
    private val backRight: Motor
    private val backLeft: Motor
    private val imu: IMU
    private val localizer: ThreeDeadWheelLocalizer
    private val ticksPerRev = 1.0
    private val wheelRadius = 1.0
    private val inchesPerTick = ticksPerRev * 2 * Math.PI * wheelRadius
    var poseEstimate = Pose2d(0.0, 0.0, 0.0)

    init {
        frontLeft = Motor("frontLeft", hardwareMap, 312)
        frontRight = Motor("frontRight", hardwareMap, 312)
        backRight = Motor("backRight", hardwareMap, 312)
        backLeft = Motor("backLeft", hardwareMap, 312)
        frontLeft.setDirection(Motor.Directions.FORWARD)
        frontRight.setDirection(Motor.Directions.REVERSE)
        backLeft.setDirection(Motor.Directions.FORWARD)
        backRight.setDirection(Motor.Directions.REVERSE)
        imu = IMU("imu", hardwareMap)
        localizer = ThreeDeadWheelLocalizer(hardwareMap, inchesPerTick)
    }

    /**
     * calls hardware reads, use once per loop
     */
    fun update() {
        val (line, angle) = localizer.update()
        var x = poseEstimate.position.x
        var y = poseEstimate.position.y
        var heading = poseEstimate.heading.toDouble()
        x += line.x[0]
        y += line.x[1]
        heading += angle[0]
        poseEstimate = Pose2d(x, y, heading)
    }

    fun setWeightedDrivePower(power: Pose2d) {
        val drive = power.position.x
        val strafe = power.position.y
        val turn = power.heading.toDouble()
        setWeightedDrivePower(drive, strafe, turn)
    }

    fun setWeightedDrivePower(drive: Double, strafe: Double, turn: Double) {
        var lfPower = drive + strafe + turn
        var rfPower = drive - strafe - turn
        var rbPower = drive + strafe - turn
        var lbPower = drive - strafe + turn
        val max = Math.max(
                Math.max(lfPower, rfPower),
                Math.max(rbPower, lbPower)
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

    fun resetIMUYaw(angle: Double) {
        imu.resetYaw(angle)
    }

    fun resetIMUYaw() {
        imu.resetYaw()
    }

    fun driveFieldCentric(power: Pose2d) {
        var vector = Vector2D(power)
        vector = vector.rotate(imu.yaw)
        setWeightedDrivePower(vector.x, vector.y, power.heading.toDouble())
    }
}
