package org.firstinspires.ftc.teamcode.util


import org.psilynx.psikit.core.Logger
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.RED
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE


object Globals {
    var robotVoltage = 13.0

    var running = true

    var alliance by SelectorInput("alliance", BLUE, RED)
    var randomization = Randomization.UNKNOWN

    val artifactDiameter = 5

    var isSimulation = false
    var unitTesting = false
    var logReplay = false

    val currentTime: Double
        get() = Logger.getTimestamp()

    enum class Alliance {
        RED, BLUE, UNKNOWN
    }
    enum class Randomization {
        GPP, PGP, PPG, UNKNOWN
    }
    enum class BallColor {
        GREEN, PURPLE, UNKNOWN
    }

}
