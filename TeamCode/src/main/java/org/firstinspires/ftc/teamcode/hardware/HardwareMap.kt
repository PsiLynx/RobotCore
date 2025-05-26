package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor

object HardwareMap{
    lateinit var hardwareMap: HardwareMap

    fun init(hardwareMap: HardwareMap){
        this.hardwareMap = hardwareMap
        hardwareMap.getAll<DcMotor>(DcMotor::class.java)
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

    class DcMotor(val name: String){
        operator fun invoke(
            direction: Component.Direction,
            basePriority: Double) = Motor(
            name,
        )
    }
}