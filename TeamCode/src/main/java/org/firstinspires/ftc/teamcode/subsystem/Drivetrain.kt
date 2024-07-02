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


    companion object{
        private const val ticksPerRev = 1.0
        private const val wheelRadius = 1.0
        private const val inchesPerTick = ticksPerRev * 2 * Math.PI * wheelRadius
    }
}
