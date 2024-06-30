package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.subsystem.ThreeDeadWheelLocalizer
import org.firstinspires.ftc.teamcode.util.Pose2D

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
        val bl = hardwareMap.get(DcMotor::class.java, "backLeft")  as FakeMotor
    override fun update(){
        val drive = (fl.speed + fr.speed) / 2.0
        val strafe = ( (fl.speed + br.speed) - drive * 2 ) / 2.0
        val turn = fl.speed - drive - strafe

        super.position.applyToEnd(Pose2D(drive, strafe, 0.0))
    }

}