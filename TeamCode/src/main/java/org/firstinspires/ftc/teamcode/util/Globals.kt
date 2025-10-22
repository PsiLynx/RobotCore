package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.psilynx.psikit.core.Logger
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.RED
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE


object Globals {
    var robotVoltage = 13.0

    var running = true

    var alliance = Alliance.UNKNOWN

    val goalPose get() =
             if(alliance == RED ) Vector2D( 61, 61)
        else if(alliance == BLUE) Vector2D(-61, 61)
        else                      Vector2D()
    var isSimulation = false
    var unitTesting = false
    var logReplay = false

    val currentTime: Double
        get() = Logger.getTimestamp()

    enum class Alliance {
        RED, BLUE, UNKNOWN
    }

}
