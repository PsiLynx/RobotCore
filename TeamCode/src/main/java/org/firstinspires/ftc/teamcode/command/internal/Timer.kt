package org.firstinspires.ftc.teamcode.command.internal

open class Timer {
    private var startTime = System.nanoTime()

    open fun restart(){ startTime = System.nanoTime() }
    open fun getDeltaTime() = ( System.nanoTime() - startTime ) * 1e-9

    open fun waitUntil(time: Double){
        while (getDeltaTime() < time) Thread.sleep(0L, 100)
    }
}