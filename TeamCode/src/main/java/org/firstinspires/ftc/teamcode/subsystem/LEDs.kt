package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.PWMLight.Color.GREEN
import org.firstinspires.ftc.teamcode.component.PWMLight.Color.AZURE
import org.firstinspires.ftc.teamcode.component.PWMLight.Color.VIOLET
import org.firstinspires.ftc.teamcode.component.PWMLight.Color.GOLD
import org.firstinspires.ftc.teamcode.component.LynxModule
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
typealias SDKLynxModule=com.qualcomm.hardware.lynx.LynxModule

object LEDs: Subsystem<LEDs>() {
    val hubs = (
        HardwareMap
            .hardwareMap!!
            .getAll(SDKLynxModule::class.java)
            .map { LynxModule { it } }
    )
    val frontLight = HardwareMap.frontLight()
    val backLight = HardwareMap.backLight()

    override val components = listOf<Component>()

    override fun update(deltaTime: Double) {
        if(Robot.readingTag){
            frontLight.color = AZURE
            backLight.color = AZURE
        }
        else if(Robot.readyToShoot){
            frontLight.color = GREEN
            backLight.color = GREEN
        }
        else {
            frontLight.color = GOLD
            backLight.color = VIOLET
        }


        hubs.forEach { hub ->
            //hub.ledColor = hueToRgb(frontLight.color.pos)
        }
    }

    /* h ∈ [0.277 , 0.772], s = 1, v = 1 */
    fun hueToRgb(h: Double): Int {
        val hDeg = ((h - 0.277) / 0.495) * 360.0

        val c = 1.0
        val x = c * (1 - kotlin.math.abs((hDeg / 60.0) % 2 - 1))
        val m = 0.0

        val (r1, g1, b1) =
            when {
                hDeg < 60  -> Triple(c, x, 0.0)
                hDeg < 120 -> Triple(x, c, 0.0)
                hDeg < 180 -> Triple(0.0, c, x)
                hDeg < 240 -> Triple(0.0, x, c)
                hDeg < 300 -> Triple(x, 0.0, c)
                else       -> Triple(c, 0.0, x)
            }

        val rI = ((r1 + m) * 255).toInt()
        val gI = ((g1 + m) * 255).toInt()
        val bI = ((b1 + m) * 255).toInt()

        return (rI shl 16) or (gI shl 8) or bI
    }
}