package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.IOComponent

abstract class SubsystemGroup<T: Subsystem<T>>(
    vararg var subsystems: Subsystem<*>
): Subsystem<T>(){
    open override val components: List<IOComponent> =
        subsystems.flatMap { it.components }

    override fun conflictsWith(other: Subsystem<*>): Boolean =
        subsystems.map {
            it.conflictsWith(other)
        }.withIndex().firstOrNull { it.value == true } != null

}