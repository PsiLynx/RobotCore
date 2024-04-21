package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Encoder
import org.firstinspires.ftc.teamcode.util.Pose2D

class ThreeDeadWheelLocalizer(
    par1Motor: DcMotor,
    par2Motor: DcMotor,
    perpMotor: DcMotor
) {
    val par1 = Encoder(par1Motor, 8192.0, wheelRadius = 24.0 / 25.4)
    val par2 = Encoder(par2Motor, 8192.0, wheelRadius = 24.0 / 25.4)
    val perp = Encoder(perpMotor, 8192.0, wheelRadius = 24.0 / 25.4)

    var position = Pose2D()
    val trackWidth = 12.0
    fun update(){
        par1.update()
        par2.update()
        perp.update()

        var deltaX = (par1.delta + par2.delta) / 2
        var deltaR = (par1.delta - par2.delta) / trackWidth
        var deltaY = perp.delta

        position += Pose2D(deltaX, deltaY, deltaR)
    }

}