package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component
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

    override val components = listOf<Component>()

    override fun update(deltaTime: Double) {
        hubs.forEach { hub ->
            if(Robot.readyToShoot){
                hub.ledColor = 0x00FF00
            }
            else if(Robot.readingTag){
                hub.ledColor = 0xFF00FF
            }
            else {
                hub.ledColor = 0xFF0000
            }
        }
    }
}