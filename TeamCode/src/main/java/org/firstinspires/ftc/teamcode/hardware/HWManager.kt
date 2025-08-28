package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.util.millis
import org.psilynx.psikit.core.Logger

object HWManager {
    val targetLooptime = millis(20.0)
    var minimumLooptime = 0.0
    val components = mutableListOf<Component>()

    lateinit var timer: Timer
    lateinit var hardwareMap: HardwareMap

    val deltaTime: Double get() = timer.getDeltaTime()

    fun init(timer: Timer){
        this.timer = timer
    }

    fun writeAll(){
        components.forEach { if(it.priority > 0) it.ioOp() }
    }
    fun loopStartFun(){
        timer.restart()
    }

    fun loopEndFun(){
        Logger.recordOutput(
            "HWManager/otherCodeTime (ms)",
            timer.getDeltaTime() * 1000)

        var sortedComponents = components.sorted().reversed()

        //println(sortedComponents.map { it.priority })
        //println(sortedComponents.filter { it.priority.isNaN() })
        //println(Logger.getTimestamp())
        components.forEach { if(it.priority > 0) it.ioOp() }
        /*
        while(
            timer.getDeltaTime() < targetLooptime
            && sortedComponents.isNotEmpty()
        ){
            val timeLeft = (
                    targetLooptime
                    - timer.getDeltaTime()
            )

            val component = sortedComponents[0]
            if(component.ioOpTime < timeLeft){
                component.ioOp()
                println(
                    "updated ${component::class.simpleName} "
                    + "${component.hashCode()}"
                )
            }
            sortedComponents = sortedComponents.slice(
                1..sortedComponents.size - 1
            )
        }
         */
        Logger.recordOutput(
            "HWManager/number of devices written",
            components.size - sortedComponents.size
        )
        Logger.recordOutput("HWManager/looptime (ms)", timer.getDeltaTime() * 1000)
        timer.waitUntil(minimumLooptime)

    }

    fun <T: Component> T.qued(): T {
        components.add(this)
        return this
    }

}