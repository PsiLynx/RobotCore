package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.sim.timeStep
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.State.Running
import org.firstinspires.ftc.teamcode.util.nanoseconds

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