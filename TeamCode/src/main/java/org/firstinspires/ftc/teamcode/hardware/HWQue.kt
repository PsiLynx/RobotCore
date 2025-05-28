package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.util.millis

object HWQue {
    val targetLooptime = millis(20.0)
    var minimumLooptime = 0.0
    val components = mutableListOf<Component>()

    lateinit var timer: Timer
    lateinit var hardwareMap: HardwareMap
    lateinit var allHubs: List<LynxModule>

    val deltaTime: Double get() = timer.getDeltaTime()

    fun init(hardwareMap: HardwareMap, timer: Timer){
        this.timer = timer
        this.hardwareMap = hardwareMap

        allHubs = hardwareMap.getAll(LynxModule::class.java)
        allHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }
    }

    fun writeAll(){
        components.forEach { if(it.priority > 0) it.ioOp() }
    }
    fun loopStartFun(){
        timer.restart()
        allHubs.forEach { it.clearBulkCache() }
    }

    fun loopEndFun(){

        var sortedComponents = components.sorted().reversed()

        //println(sortedComponents.map { it.priority })
        //println(sortedComponents.filter { it.priority.isNaN() })
        while(
            timer.getDeltaTime() < targetLooptime
            && sortedComponents.isNotEmpty()
            && sortedComponents[0].priority > 0
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

    fun <T: Component> T.qued(): T {
        components.add(this)
        return this
    }

}