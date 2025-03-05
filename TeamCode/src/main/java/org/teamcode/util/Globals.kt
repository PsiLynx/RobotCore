package org.teamcode.util

object Globals {
    var robotVoltage = 13.0

    var state = State.Running

    enum class State {
        Testing, Running
    }

}
