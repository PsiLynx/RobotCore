package org.ftc3825.util

object Globals {
    var robotVoltage = 0.0

    var timeSinceStart = 0.0

    var state = State.Running

    var AUTO = false

    enum class State {
        Testing, Running
    }

}
