package org.firstinspires.ftc.teamcode.subsystem.internal

import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor

interface SubsystemIO {
    fun update(deltaTime: Double = 0.0)
    fun reset()
}

abstract class RealSubsystemIO : SubsystemIO {
    protected val components: List<Component> = listOf()
    protected val motors: ArrayList<Motor>
        get() = with(arrayListOf<Component>()) {
            addAll(components.filter { it is Motor && it !is CRServo } )
            return this as ArrayList<Motor>
        }
    override fun update(deltaTime: Double) = components.forEach { it.update(deltaTime) }
    override fun reset() = components.forEach { it.reset() }
}