package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.util.isWithin
import org.firstinspires.ftc.teamcode.util.of

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