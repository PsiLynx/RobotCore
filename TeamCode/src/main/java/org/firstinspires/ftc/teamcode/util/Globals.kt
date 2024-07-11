package org.firstinspires.ftc.teamcode.util

object Globals {
    var robotVoltage = 0.0

    var timeSinceStart = 0.0

    var state = State.running

    enum class State(){
        testing, running
    }

}