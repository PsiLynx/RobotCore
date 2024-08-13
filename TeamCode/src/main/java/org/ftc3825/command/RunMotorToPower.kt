package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.component.Motor
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.isWithin
import org.ftc3825.util.of

class RunMotorToPower(val power: Double, var subsystem: Subsystem, var motor: Motor): Command(
    initialize = { motor.setPower(power); println(power) },
    end = {_ -> motor.setPower(0.0)}

) {
    private var loops = 0
    init {
        addRequirement(subsystem, write=true)
    }

    override fun isFinished() = motor.acceleration isWithin 1 of 0 and (loops > 50)
    override fun execute(){
        loops ++
        motor.setPower(power)
    }

}