package org.firstinspires.ftc.teamcode.hardware

import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.Pinpoint
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.util.millis
import kotlin.math.PI

object HWQue {
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
    ) = managed(CRServo(name, direction, ticksPerRev))

    fun motor(
        name: String,
        direction: Component.Direction = Component.Direction.FORWARD,
        basePriority: Double = 1.0,
        priorityScale: Double = 1.0,
    ) = managed(Motor(name, direction, basePriority, priorityScale))

    fun pinpoint(name: String, priority: Double)
        = managed(Pinpoint(name, priority))

    fun servo(
        name: String,
        basePriority: Double,
        priorityScale: Double,
        range: Servo.Range
    ) = managed(Servo(name, basePriority, priorityScale, range))

}