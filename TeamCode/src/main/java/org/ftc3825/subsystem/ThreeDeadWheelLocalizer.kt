package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.component.Encoder
import org.ftc3825.util.Pose2D
import org.ftc3825.util.inches
import org.ftc3825.util.millimeters
import kotlin.math.PI

open class ThreeDeadWheelLocalizer(
    par1Motor: DcMotor,
    par2Motor: DcMotor,
    perpMotor: DcMotor
) {
    private val par1YTicks = 1
    private val par2YTicks = 2
    private val perpXTicks = 3

    private val ticksPerRev = 2000.0
    private val inPerTick = 2.4 * 2 * PI / 2.54 / 2000.0

    val par1 = Encoder(par1Motor, ticksPerRev, wheelRadius = millimeters(24))
    val par2 = Encoder(par2Motor, ticksPerRev, wheelRadius = millimeters(24))
    val perp = Encoder(perpMotor, ticksPerRev, wheelRadius = millimeters(24))



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