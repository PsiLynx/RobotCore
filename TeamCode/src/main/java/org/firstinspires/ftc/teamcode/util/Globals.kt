package org.firstinspires.ftc.teamcode.util


import org.psilynx.psikit.core.Logger
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.RED
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE


object Globals {
    var robotVoltage = 13.0

    var running = true

    var alliance by SelectorInput("alliance", BLUE, RED)

    val artifactDiameter = 5

    var fake = false
    var unitTesting = false
    var logReplay = false

    val currentTime: Double
        get() = Logger.getTimestamp()

    enum class Alliance {
        RED, BLUE, UNKNOWN
    }

    enum class Side {
        AUDIENCE_SIDE, // audience side is where the red garden is ( -y side )
        FAR_SIDE, // far side is where the blue garden is. ( +y side )
        // the red hive starts tipped up towards audience side, and vise versa.
    }

}
