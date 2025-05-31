package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.logging.Replayer
import org.firstinspires.ftc.teamcode.sim.FakeTimer

object Globals {
    var robotVoltage = 13.0

    var running = true

    var unitTesting = false
    var logReplay = false

    private var startTime = System.nanoTime()
    val currentTime: Double
        get() = (
            if(running) ( System.nanoTime() - startTime ) * 1E-9
            else if(unitTesting) FakeTimer.time
            else if(logReplay) Replayer.currentTime
            else error(
                "can't get current time if running, unitTesting and "
                + "logReplay are all false"
            )
        )


}
