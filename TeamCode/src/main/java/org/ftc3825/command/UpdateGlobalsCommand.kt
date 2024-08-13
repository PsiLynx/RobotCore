package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.sim.timeStep
import org.ftc3825.subsystem.Robot
import org.ftc3825.util.Globals
import org.ftc3825.util.Globals.State.Running
import org.ftc3825.util.nanoseconds

class UpdateGlobalsCommand: Command() {

    var startTime = nanoseconds( System.nanoTime() )

    init {
        addRequirement(Robot, write = false)
    }
    override fun execute() {
        Globals.robotVoltage = Robot.voltage

        if(Globals.state == Running){
            Globals.timeSinceStart = nanoseconds(System.nanoTime()) - startTime
        }
        else { Globals.timeSinceStart += timeStep }
    }
}