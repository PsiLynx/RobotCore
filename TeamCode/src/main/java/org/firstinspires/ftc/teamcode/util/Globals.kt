package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.psilynx.psikit.core.Logger

object Globals {
    var robotVoltage = 13.0

    var running = true

    var isSimulation = false
    var unitTesting = false
    var logReplay = false

    val currentTime: Double
        get() = Logger.getTimestamp()

}
