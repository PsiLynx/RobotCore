package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Encoder
import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.inches
import org.firstinspires.ftc.teamcode.util.millimeters

open class ThreeDeadWheelLocalizer(
    par1Motor: DcMotor,
    par2Motor: DcMotor,
    perpMotor: DcMotor
) {
    val par1 = Encoder(par1Motor, 8192.0, wheelRadius = millimeters(24))
    val par2 = Encoder(par2Motor, 8192.0, wheelRadius = millimeters(24))
    val perp = Encoder(perpMotor, 8192.0, wheelRadius = millimeters(24))

    var position = Pose2D()
    val trackWidth = inches(12.0)
    open fun update(){
        par1.update()
        par2.update()
        perp.update()

        val deltaY = (par1.delta + par2.delta) / 2
        val deltaR = (par1.delta - par2.delta) / trackWidth
        val deltaX = perp.delta

        position.applyToEnd(Pose2D(deltaX, deltaY, deltaR))
    }

}