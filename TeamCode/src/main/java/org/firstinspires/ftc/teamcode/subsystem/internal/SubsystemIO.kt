package org.firstinspires.ftc.teamcode.subsystem.internal

import org.firstinspires.ftc.teamcode.component.Component

interface SubsystemIO {
    fun update(deltaTime: Double = 0.0)
    fun reset()
}

interface RealSubsystemIO : SubsystemIO {
    val components: List<Component>
    override fun update(deltaTime: Double) = components.forEach { it.update(deltaTime) }
    override fun reset() = components.forEach { it.reset() }
}