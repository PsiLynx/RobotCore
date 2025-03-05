package org.teamcode.command

import org.teamcode.command.internal.Command
import org.teamcode.component.Motor
import org.teamcode.subsystem.Subsystem
import kotlin.math.abs

class RunMotorToPower(val power: Double, var subsystem: Subsystem<*>, var motor: Motor): Command(
    end = {_ -> motor.power = 0.0}

) {
    private var loops = 0
    init {
        addRequirement(subsystem)
    }

    override fun isFinished() = (
        abs(motor.acceleration) < 10000 && (loops > 50)
    )

    override fun execute(){
        loops ++
        motor.power = power
    }

}