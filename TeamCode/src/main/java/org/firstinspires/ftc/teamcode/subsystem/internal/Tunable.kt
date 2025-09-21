package org.firstinspires.ftc.teamcode.subsystem.internal

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.State

/**
 * Tunables must either be objects or have a no parameter constructor
 */
interface Tunable<T: State<T>>{
    val tuningForward: State<T>
    val tuningBack: State<T>

    /**
     *  this should only accept State<T>, but it can't because of type erasure
     */
    val tuningCommand: (State<*>) -> Command

}