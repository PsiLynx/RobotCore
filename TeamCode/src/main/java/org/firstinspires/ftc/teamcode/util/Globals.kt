package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.sim.FakeTimer

object Globals {
    var robotVoltage = 13.0

    var state = State.Running

    val currentTime: Double
        get() = when(state) {
            State.Running -> System.nanoTime() * 1E-9
            State.Testing -> FakeTimer.time
        }

    enum class State {
        Testing, Running
    }

}
