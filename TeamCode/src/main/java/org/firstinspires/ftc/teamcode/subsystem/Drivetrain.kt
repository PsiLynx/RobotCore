package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.util.Pose2D

object Drivetrain : Subsystem {
    private const val ticksPerRev = 1.0
    private const val wheelRadius = 1.0
    private const val metersPerTick = ticksPerRev * 2 * Math.PI * wheelRadius

    lateinit var frontLeft: Motor
    lateinit var frontRight: Motor
    lateinit var backRight: Motor
    lateinit var backLeft: Motor

    override fun init(hardwareMap: HardwareMap) {
        frontLeft = Motor("frontLeft", hardwareMap, 312)
        frontRight = Motor("frontRight", hardwareMap, 312)
        backRight = Motor("backRight", hardwareMap, 312)
        backLeft = Motor("backLeft", hardwareMap, 312)

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


}
