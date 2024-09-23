package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.ThreeDeadWheelLocalizer
import org.ftc3825.util.Pose2D
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName

class FakeLocalizer(
        val hardwareMap: FakeHardwareMap
):
    ThreeDeadWheelLocalizer(
        hardwareMap.get(DcMotor::class.java, "unused"),
        hardwareMap.get(DcMotor::class.java, "unused"),
        hardwareMap.get(DcMotor::class.java, "unused")
    ) {

    override fun update(){
        val fl = (hardwareMap.get(DcMotor::class.java, flMotorName) as FakeMotor)
        val fr = (hardwareMap.get(DcMotor::class.java, frMotorName) as FakeMotor)
        val br = (hardwareMap.get(DcMotor::class.java, brMotorName) as FakeMotor)

        var flSpeed = fl.speed * fl.maxVelocityInTicksPerSecond / 312
        var frSpeed = fr.speed * fr.maxVelocityInTicksPerSecond / 312 * -1
        var brSpeed = br.speed * br.maxVelocityInTicksPerSecond / 312 * -1

        val drive = (flSpeed + frSpeed) / 2.0
        val strafe = ( (flSpeed + brSpeed) - drive * 2 ) / 2.0
        val turn = flSpeed - drive - strafe

        super.position.applyToEnd(Pose2D(drive, strafe, 0))
    }

}