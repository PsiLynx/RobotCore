package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.util.Globals

open class Timer {
    private var startTime = Globals.currentTime

    open fun restart(){ startTime = Globals.currentTime }
    open fun getDeltaTime() = Globals.currentTime - startTime

}