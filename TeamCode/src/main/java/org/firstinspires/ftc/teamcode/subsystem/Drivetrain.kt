package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.GVF.PathSegment
import org.firstinspires.ftc.teamcode.component.IMU
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeLocalizer
import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Rotation2D

class Drivetrain(hardwareMap: HardwareMap) : Subsystem(hardwareMap) {
    private val frontLeft  = Motor("frontLeft", hardwareMap, 312)
    private val frontRight = Motor("frontRight", hardwareMap, 312)
    private val backRight  = Motor("backRight", hardwareMap, 312)
    private val backLeft   = Motor("backLeft", hardwareMap, 312)
    private val imu = IMU("imu", hardwareMap)
//    private val localizer = ThreeDeadWheelLocalizer(
//        frontLeft.motor,
//        backRight.motor,
//        frontRight.motor
//    )
    private val localizer = FakeLocalizer(
        hardwareMap as FakeHardwareMap
    )

    val position: Pose2D
        get() = localizer.position

    init {
        frontLeft.setDirection(Motor.FORWARD)
        frontRight.setDirection(Motor.REVERSE)
        backLeft.setDirection(Motor.FORWARD)
        backRight.setDirection(Motor.REVERSE)
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
        power.vector.rotate(imu.yaw)
        setWeightedDrivePower(power)
    }

    fun follow(segment: PathSegment){
        localizer.update()
        setWeightedDrivePower(segment.moveDir(position.vector) + Rotation2D(), )
    }
    fun follow(path: Path){
        localizer.update()
        setWeightedDrivePower(path.vector(position) + Rotation2D())
    }

    companion object{
        private const val ticksPerRev = 1.0
        private const val wheelRadius = 1.0
        private const val inchesPerTick = ticksPerRev * 2 * Math.PI * wheelRadius
    }
}
