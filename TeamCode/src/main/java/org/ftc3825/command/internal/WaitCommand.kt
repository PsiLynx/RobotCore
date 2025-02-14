package org.ftc3825.command.internal

open class WaitCommand(var seconds: Number): Command(
    name = { "WaitCommand" }
) {

    var start = 0L
    override fun initialize(){
        start = System.nanoTime()
    }

    override fun isFinished() = (
        (System.nanoTime() - start) > ( seconds.toDouble() * 1e9 )
    )
}
