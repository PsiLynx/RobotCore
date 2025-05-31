package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.logging.Input
import org.firstinspires.ftc.teamcode.logging.InputData
import org.firstinspires.ftc.teamcode.util.millis
import org.firstinspires.ftc.teamcode.util.nanoseconds
import kotlin.time.measureTime

object HWManager {
    val targetLooptime = millis(20.0)
    var minimumLooptime = 0.0
    val components = mutableListOf<Component>()

    lateinit var timer: Timer
    lateinit var hardwareMap: HardwareMap
    lateinit var allHubs: List<LynxModule>

    object BulkData: Input {
        override val uniqueName = "bulkDataInput"

        var timeTaken = 0.0
        lateinit var data: LynxModule.BulkData
        val dataList = Array(21) { 0.0 }.toMutableList()

        fun updateBulkData() {
            timeTaken = nanoseconds(
                measureTime {
                    data = allHubs.first { it.isParent }.bulkData
                }.inWholeNanoseconds
            )
            getValue().withIndex().forEach {
                dataList[it.index] = it.value
            }
        }
        override fun getRealValue() = Serialized(timeTaken, data).toArray()

        var quadrature = ListView(1, 4, dataList)
        var digital = ListView(5, 12, dataList)
        var analog = ListView(13, 16, dataList)
        var overCurrent = ListView(17, 20, dataList)


        data class Serialized(
            val time: Double,
            val data: LynxModule.BulkData
        ): InputData(){
            override fun toArray() = arrayOf(
                time,
                data.getMotorCurrentPosition(0).toDouble(),
                data.getMotorCurrentPosition(1).toDouble(),
                data.getMotorCurrentPosition(2).toDouble(),
                data.getMotorCurrentPosition(3).toDouble(),
                if(data.getDigitalChannelState(0)) 1.0 else 0.0,
                if(data.getDigitalChannelState(1)) 1.0 else 0.0,
                if(data.getDigitalChannelState(2)) 1.0 else 0.0,
                if(data.getDigitalChannelState(3)) 1.0 else 0.0,
                if(data.getDigitalChannelState(4)) 1.0 else 0.0,
                if(data.getDigitalChannelState(5)) 1.0 else 0.0,
                if(data.getDigitalChannelState(6)) 1.0 else 0.0,
                if(data.getDigitalChannelState(7)) 1.0 else 0.0,
                data.getAnalogInputVoltage(0),
                data.getAnalogInputVoltage(1),
                data.getAnalogInputVoltage(2),
                data.getAnalogInputVoltage(3),
                if(data.isMotorOverCurrent(0)) 1.0 else 0.0,
                if(data.isMotorOverCurrent(1)) 1.0 else 0.0,
                if(data.isMotorOverCurrent(2)) 1.0 else 0.0,
                if(data.isMotorOverCurrent(3)) 1.0 else 0.0,
            )
        }
    }

    val deltaTime: Double get() = timer.getDeltaTime()

    fun init(hardwareMap: HardwareMap, timer: Timer){
        this.timer = timer
        this.hardwareMap = hardwareMap

        allHubs = hardwareMap.getAll(LynxModule::class.java)
        allHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }
        BulkData.logged()
    }

    fun writeAll(){
        components.forEach { if(it.priority > 0) it.timedIoOp() }
    }
    fun loopStartFun(){
        timer.restart()
        allHubs.forEach { it.clearBulkCache() }
        BulkData.updateBulkData()
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
                sortedComponents[0].timedIoOp()
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