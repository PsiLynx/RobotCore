package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.command.internal.Timer
import kotlin.collections.sorted
import kotlin.math.PI

object HWManager {
    const val targetLooptimeMS = 20.0
    val components = mutableListOf<Component>()

    var timer = Timer()

    fun loopStartFun() = timer.restart()

    fun loopEndFun(){
        var sortedComponents = components.sorted()

        while(
            timer.getDeltaTime() < targetLooptimeMS / 1000
            && sortedComponents.isNotEmpty()
        ){
            val timeLeft = (
                    targetLooptimeMS / 1000
                    - timer.getDeltaTime()
            )

            if(sortedComponents[0].ioOpTimeMs / 1000 < timeLeft){
                sortedComponents[0].ioOp()
            }
            sortedComponents = sortedComponents.slice(
                1..sortedComponents.size - 1
            )
        }

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