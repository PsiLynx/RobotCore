package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.subsystem.ThreeDeadWheelLocalizer
import org.ftc3825.util.Pose2D

class FakeLocalizer(
        hardwareMap: FakeHardwareMap
):
    ThreeDeadWheelLocalizer(
        hardwareMap.get(DcMotor::class.java, "unused"),
        hardwareMap.get(DcMotor::class.java, "unused"),
        hardwareMap.get(DcMotor::class.java, "unused")
    ) {

        val fl = hardwareMap.get(DcMotor::class.java, "frontLeft") as FakeMotor
        val fr = hardwareMap.get(DcMotor::class.java, "frontRight")as FakeMotor
        val br = hardwareMap.get(DcMotor::class.java, "backRight") as FakeMotor
    override fun update(){
        val drive = (fl.speed + fr.speed) / 2.0
        val strafe = ( (fl.speed + br.speed) - drive * 2 ) / 2.0
        val turn = fl.speed - drive - strafe

        super.position.applyToEnd(Pose2D(drive, strafe, 0))
    }

}