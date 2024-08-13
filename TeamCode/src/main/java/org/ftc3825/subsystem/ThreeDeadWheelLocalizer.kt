package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.component.Encoder
import org.ftc3825.util.Pose2D
import org.ftc3825.util.inches
import org.ftc3825.util.millimeters

open class ThreeDeadWheelLocalizer(
    par1Motor: DcMotor,
    par2Motor: DcMotor,
    perpMotor: DcMotor
) {
    private val trackWidth = inches(12.0)

    private val par1 = Encoder(par1Motor, 8192.0, wheelRadius = millimeters(24))
    private val par2 = Encoder(par2Motor, 8192.0, wheelRadius = millimeters(24))
    private val perp = Encoder(perpMotor, 8192.0, wheelRadius = millimeters(24))

    var position = Pose2D()
    open fun update(){
        par1.update()
        par2.update()
        perp.update()

        val deltaX = perp.delta
        val deltaY = (par1.delta + par2.delta) / 2
        val deltaR = (par1.delta - par2.delta) / trackWidth

        position.applyToEnd(Pose2D(deltaX, deltaY, deltaR))
    }

}