package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.psilynx.psikit.core.Logger
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.RED
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE


object Globals {
    var robotVoltage = 13.0

    var running = true

    val alliance by SelectorInput("alliance", RED, BLUE)

    val goalPose get() =
             if(alliance == RED ) Vector3D( 61, 61,31)
        else if(alliance == BLUE) Vector3D(-61, 61,31)
        else Vector3D()

    //Shooter globals:
    var flywheelOffset = Vector2D(-7,15)

    var isSimulation = false
    var unitTesting = false
    var logReplay = false

    val currentTime: Double
        get() = Logger.getTimestamp()

    enum class Alliance {
        RED, BLUE, UNKNOWN
    }

}
