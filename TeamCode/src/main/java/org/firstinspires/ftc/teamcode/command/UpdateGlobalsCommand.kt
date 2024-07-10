package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.Globals

class UpdateGlobalsCommand: Command() {
    override fun initialize() {
        addReqirement(Robot, write = false)
    }
    override fun execute() {
        Globals.robotVoltage = Robot.voltage
    }
}