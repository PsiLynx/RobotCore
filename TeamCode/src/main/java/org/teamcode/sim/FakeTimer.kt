package org.teamcode.sim

import org.teamcode.command.internal.Timer

class FakeTimer(private val deltaTime: Double): Timer() {
    override fun restart() { }
    override fun getDeltaTime() = deltaTime
}