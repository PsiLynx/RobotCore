package org.ftc3825.command.internal

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.reflection.FieldProvider
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import kotlin.reflect.jvm.javaField

object GlobalHardwareMap{
    lateinit var hardwareMap: HardwareMap

    fun init(hardwareMap: HardwareMap){
        this.hardwareMap = hardwareMap
    }

    //fun get(deviceName: String) = hardwareMap.get(deviceName)

    inline fun <reified T: Any> get(classOrInterface: Class<out T>, deviceName: String): T
        = hardwareMap.get(classOrInterface, deviceName)

    fun getIdentifier(name: String, defType: String, defPackage: String) =
        hardwareMap.appContext.resources.getIdentifier(name, defType, defPackage)
    object appContext {
        val packageName: String
            get() = hardwareMap.appContext.packageName
    }
}