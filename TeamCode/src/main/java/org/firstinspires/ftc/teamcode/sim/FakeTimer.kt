package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.internal.Timer

object FakeTimer: Timer() {
    private var time = 0.0
    override fun restart() {
        time = 0.0
    }

    override fun getDeltaTime() = time
    fun addTime(time: Double) {
        this.time += time
    }

    override fun waitUntil(time: Double) {
        if(time > this.time) this.time = time
    }
}