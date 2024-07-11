package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.sim.timeStep

import org.firstinspires.ftc.teamcode.util.Globals.State.running
import org.firstinspires.ftc.teamcode.util.Globals.State.testing
import org.firstinspires.ftc.teamcode.util.nanoseconds

class UpdateGlobalsCommand: Command() {

    var startTime = nanoseconds( System.nanoTime() )

    override fun initialize() {
        addRequirement(Robot, write = false)
    }
    override fun execute() {
        Globals.robotVoltage = Robot.voltage

        if(Globals.state == running){
            Globals.timeSinceStart = nanoseconds(System.nanoTime()) - startTime
        }
        else{
            Globals.timeSinceStart += timeStep
        }
    }
}