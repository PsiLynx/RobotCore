package org.ftc3825.sim

import org.ftc3825.command.internal.Timer

class FakeTimer(private val deltaTime: Double): Timer() {
    override fun restart() { }
    override fun getDeltaTime() = deltaTime
}