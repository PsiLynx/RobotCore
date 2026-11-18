package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.internal.Timer

class FakeTimer(): Timer() {
    override fun restart() {
        time += loopInterval
    }

    override fun getDeltaTime() = loopInterval


    companion object {
        var loopInterval = 0.01
        var time = 0.0
    }
}