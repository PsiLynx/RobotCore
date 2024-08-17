package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
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
        val fl = (hardwareMap.get(DcMotor::class.java, flMotorName) as FakeMotor).speed
        val fr = (hardwareMap.get(DcMotor::class.java, frMotorName) as FakeMotor).speed * -1
        val br = (hardwareMap.get(DcMotor::class.java, brMotorName) as FakeMotor).speed * -1

        val drive = (fl + fr) / 2.0
        val strafe = ( (fl + br) - drive * 2 ) / 2.0
        val turn = fl - drive - strafe
        println(fl)

        super.position.applyToEnd(Pose2D(drive, strafe, 0))
    }

}