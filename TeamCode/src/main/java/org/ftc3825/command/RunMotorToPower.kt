package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.component.Motor
import org.ftc3825.subsystem.Subsystem
import kotlin.math.abs

class RunMotorToPower(val power: Double, var subsystem: Subsystem<*>, var motor: Motor): Command(
    end = {_ -> motor.setPower(0.0)}

) {
    private var loops = 0
    init {
        addRequirement(subsystem, write=true)
    }

    override fun isFinished() = (
        abs(motor.acceleration) < 10000 && (loops > 50)
    )

    override fun execute(){
        loops ++
        motor.setPower(power)
    }

}