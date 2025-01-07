package org.ftc3825.fakehardware


import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName

class FakeLocalizer(
        val hardwareMap: FakeHardwareMap
){

    var position = Pose2D()
     fun update(){
        val fl = (hardwareMap.get(DcMotor::class.java, flMotorName) as FakeMotor)
        val fr = (hardwareMap.get(DcMotor::class.java, frMotorName) as FakeMotor)
        val br = (hardwareMap.get(DcMotor::class.java, brMotorName) as FakeMotor)

        val flSpeed = fl.speed * fl.maxVelocityInTicksPerSecond / 312
        val frSpeed = fr.speed * fr.maxVelocityInTicksPerSecond / 312 * -1
        val brSpeed = br.speed * br.maxVelocityInTicksPerSecond / 312 * -1

        val drive = (flSpeed + frSpeed) / 2.0
        val strafe = ( (flSpeed + brSpeed) - drive * 2 ) / 2.0
        val turn = flSpeed - drive - strafe

        position.applyToEnd(Pose2D(drive, strafe, 0))
    }

}
