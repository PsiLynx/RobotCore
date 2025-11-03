package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap

object Cameras {
    val obeliskCamera = HardwareMap.obeliskCamera(
        Vector2D(640, 480),
    )
}