package org.firstinspires.ftc.teamcode.command.internal.controlFlow

import org.firstinspires.ftc.teamcode.command.internal.Command
import java.util.function.Supplier

class If(
    condition: () -> Boolean,
    command: Command
): SelectCommand<Boolean>( Supplier { condition() }, { true runs command } ) {
    infix fun Else(command: Command): SelectCommand<Boolean> {
        if(commands[false] is If){
            (commands[false] as If) Else command
        }
        else {
            false runs command
        }
        return this
    }

    fun elseIf(condition: () -> Boolean, command: Command): If{
        if(commands[false] is If){
            commands[false].run { false runs If(condition, command) }
        }
        else {
            false runs If(condition, command)
        }
        return this
    }
}