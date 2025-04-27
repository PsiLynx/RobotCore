package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareMap

object GlobalHardwareMap{
    lateinit var hardwareMap: HardwareMap

    fun init(hardwareMap: HardwareMap){
        this.hardwareMap = hardwareMap
    }

    //fun get(deviceName: String) = hardwareMap.get(deviceName)

    inline fun <reified T: Any> get(
        classOrInterface: Class<out T>,
        deviceName: String
    ): T {
        return hardwareMap.get(classOrInterface, deviceName)
    }

    fun getIdentifier(name: String, defType: String, defPackage: String) =
        hardwareMap.appContext.resources.getIdentifier(name, defType, defPackage)
    object appContext {
        val packageName: String
            get() = hardwareMap.appContext.packageName
    }
}