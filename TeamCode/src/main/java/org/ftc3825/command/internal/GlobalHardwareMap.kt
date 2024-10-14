package org.ftc3825.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap

object GlobalHardwareMap{
    lateinit var hardwareMap: HardwareMap

    fun init(hardwareMap: HardwareMap){
        this.hardwareMap = hardwareMap
    }

    fun get(deviceName: String) = hardwareMap.get(deviceName)

    fun <T: Any> get(classOrInterface: Class<out T>, deviceName: String)
        = hardwareMap.get(classOrInterface, deviceName)
}