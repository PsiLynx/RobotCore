package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.internal.Timer

class FakeTimer(private val deltaTime: Double): Timer() {
    override fun restart() { }
    override fun getDeltaTime() = deltaTime
}