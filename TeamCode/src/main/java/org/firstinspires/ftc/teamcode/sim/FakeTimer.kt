package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.internal.Timer

class FakeTimer(): Timer() {
    private var resetTime = 0.0
    override fun restart() {
        resetTime = Companion.time
    }

    override fun getDeltaTime() = Companion.time - resetTime

    override fun waitUntil(time: Double) =
        if(time > getDeltaTime()) addTime(time - getDeltaTime())
        else {}

    companion object {
        fun addTime(time: Double) {
            this.time += time
        }
        var time = 0.0
    }
}