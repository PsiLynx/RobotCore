package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.Motor
import org.ftc3825.component.Motor.Direction.FORWARD
import org.ftc3825.component.Motor.Direction.REVERSE
import org.ftc3825.util.Pose2D
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName

object Drivetrain : Subsystem {
    override var initialized = false

    lateinit var frontLeft: Motor
    lateinit var frontRight: Motor
    lateinit var backRight: Motor
    lateinit var backLeft: Motor

    override fun init(hardwareMap: HardwareMap) {
        if(!initialized) {
            frontLeft = Motor(flMotorName, hardwareMap, 312, direction = FORWARD)
            frontRight = Motor(frMotorName, hardwareMap, 312, direction = REVERSE)
            backLeft = Motor(blMotorName, hardwareMap, 312, direction = FORWARD)
            backRight = Motor(brMotorName, hardwareMap, 312, direction = REVERSE)

            frontLeft.useInternalEncoder()
            frontRight.useInternalEncoder()
            backLeft.useInternalEncoder()
            backRight.useInternalEncoder()
        }
        initialized = true
    }

    override fun update(deltaTime: Double) {
        motors.forEach { it.update(deltaTime) }
    }

    override val motors: ArrayList<Motor>
        get() = arrayListOf(frontLeft, frontRight, backLeft, backRight)

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
