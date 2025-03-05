package org.teamcode.sim

import org.teamcode.command.internal.RunCommand

fun addRule(rule: () -> Unit) {
    RunCommand(command = rule).schedule()
}