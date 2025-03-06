package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.internal.RunCommand

fun addRule(rule: () -> Unit) {
    RunCommand(command = rule).schedule()
}