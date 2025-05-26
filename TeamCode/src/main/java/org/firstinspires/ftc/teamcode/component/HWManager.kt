package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.util.millis
import kotlin.collections.sorted
import kotlin.math.PI

object HWManager {
    val targetLooptime = millis(200.0)
    var minimumLooptime = 0.0
    val components = mutableListOf<Component>()

    lateinit var timer: Timer

    val deltaTime: Double get() = timer.getDeltaTime()

    fun init(timer: Timer){ this.timer = timer }

    fun loopStartFun() = timer.restart()

    fun loopEndFun(){
        var sortedComponents = components.sorted()

        while(
            timer.getDeltaTime() < targetLooptime
            && sortedComponents.isNotEmpty()
        ){
            val timeLeft = (
                    targetLooptime
                    - timer.getDeltaTime()
            )

            if(sortedComponents[0].ioOpTime < timeLeft){
                sortedComponents[0].ioOp()
            }
            sortedComponents = sortedComponents.slice(
                1..sortedComponents.size - 1
            )
        }
        timer.waitUntil(minimumLooptime)

    }

    fun <T: Component> managed(device: T): T {
        components.add(device)
        return device
    }

    fun crServo(
        name: String,
        direction: Component.Direction,
        ticksPerRev: Double = 1.0,
        wheelRadius: Double = 1 / ( PI * 2 ),
    ) = managed(CRServo(name, direction, ticksPerRev, wheelRadius))

    fun motor(
        name: String,
        rpm: Int,
        direction: Component.Direction = Component.Direction.FORWARD,
        wheelRadius: Double = 1.0,
    ) = managed(Motor(name, rpm, direction, wheelRadius))

    fun pinpoint(name: String, priority: Double)
        = managed(Pinpoint(name, priority))

    fun servo(
        name: String,
        basePriority: Double,
        priorityScale: Double,
        range: Servo.Range
    ) = managed(Servo(name, basePriority, priorityScale, range))

}