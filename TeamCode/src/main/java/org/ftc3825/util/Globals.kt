package org.ftc3825.util

object Globals {
    var robotVoltage = 13.0

    var state = State.Running

    enum class State {
        Testing, Running
    }

}
