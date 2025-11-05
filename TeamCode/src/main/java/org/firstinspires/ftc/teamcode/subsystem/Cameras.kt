package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.GPP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PGP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PPG

object Cameras: Subsystem<Cameras>() {
    val obeliskCamera = HardwareMap.obeliskCamera(
        Vector2D(640, 480)
    )
    override val components = listOf<Component>()

    override fun update(deltaTime: Double) {
        obeliskCamera.detections.forEach {
            when(it.id){
                21 -> Globals.randomization = GPP
                22 -> Globals.randomization = PGP
                23 -> Globals.randomization = PPG
            }
        }
    }


}