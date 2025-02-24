package org.ftc3825.command.internal

open class Timer {
    private var startTime = System.nanoTime()
    open fun restart(){ startTime = System.nanoTime() }
    open fun getDeltaTime() = ( System.nanoTime() - startTime ) / 1e-9
}