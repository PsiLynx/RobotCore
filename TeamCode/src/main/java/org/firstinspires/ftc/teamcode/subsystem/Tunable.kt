package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.command.internal.Command

/**
 * Tunables must either be objects or have a no parameter constructor
 */
abstract class Tunable(){
    abstract val to: Command
    abstract val fro: Command
}
