package org.ftc3825.sim

import org.ftc3825.command.internal.RunCommand

fun addRule(rule: () -> Unit) {
    RunCommand(command = rule).schedule()
}