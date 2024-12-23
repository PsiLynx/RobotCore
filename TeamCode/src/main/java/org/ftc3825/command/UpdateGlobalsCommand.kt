package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.sim.timeStep
import org.ftc3825.subsystem.Robot
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.Globals
import org.ftc3825.util.Globals.State.Running
import org.ftc3825.util.nanoseconds

class UpdateGlobalsCommand: Command() {

    private var startTime = nanoseconds( System.nanoTime() )

    override val requirements = mutableSetOf<Subsystem<*>>(Robot)

    override fun execute() {
        //Globals.robotVoltage = Robot.voltage

        if(Globals.state == Running){
            Globals.timeSinceStart = nanoseconds(System.nanoTime()) - startTime
        }
        else { Globals.timeSinceStart += timeStep }
    }

    override fun isFinished() = false

    override var name = "UpdateGlobalsCommand"
    override var description = { "" }
}
