package org.teamcode.fakehardware


import com.qualcomm.robotcore.hardware.DcMotor
import org.teamcode.util.geometry.Pose2D
import org.teamcode.util.brMotorName
import org.teamcode.util.flMotorName
import org.teamcode.util.frMotorName

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
