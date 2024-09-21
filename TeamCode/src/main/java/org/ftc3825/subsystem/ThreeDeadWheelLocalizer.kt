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
    private val par1YTicks = 0
    private val par2YTicks = 0
    private val perpXTicks = 0

    private val ticksPerIn = 8192.0
    private val inPerTick = 1 / ticksPerIn

    private val par1 = Encoder(par1Motor, inPerTick, wheelRadius = millimeters(24))
    private val par2 = Encoder(par2Motor, inPerTick, wheelRadius = millimeters(24))
    private val perp = Encoder(perpMotor, inPerTick, wheelRadius = millimeters(24))



    var position = Pose2D()
    open fun update(){
        par1.update()
        par2.update()
        perp.update()

        val deltaX = (
                (par1YTicks * par1.delta - par2YTicks * par1.delta)
                / (par1YTicks - par2YTicks)
        ) * inPerTick
        val deltaY = (
                perpXTicks / (par1YTicks - par2YTicks)
                * (par2.delta - par1.delta)
                + perp.delta
            ) * inPerTick

        val deltaR = (par1.delta - par2.delta) / (par1YTicks - par2YTicks)

        position.applyToEnd(Pose2D(deltaX, deltaY, deltaR))
    }

}