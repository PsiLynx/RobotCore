package org.firstinspires.ftc.teamcode.sim

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardware
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.HardwareMapInterface
import org.firstinspires.ftc.teamcode.util.Globals

object SimulatedHardwareMap: HardwareMap(null, null), HardwareMapInterface {
    override var lastTime = Globals.timeSinceStart
    override var devices = mutableMapOf<String, FakeHardware>()

    override fun <T : Any?> get(classOrInterface: Class<out T>?, deviceName: String?): T {
        if(deviceName == null) throw IllegalArgumentException("device name cannot be null in fakeHardwareMap.get()")
        if (
            devices[deviceName] != null
        ) return devices[deviceName] as T
        else {
            with(
                when (classOrInterface) {
                    DcMotor::class.java -> SimulatedMotor(
                        DataAnalyzer.motors[deviceName]!!
                    )
                    else -> throw IllegalArgumentException(String.format("Unable to find a hardware device with name \"%s\" and type %s", deviceName, classOrInterface?.getSimpleName()))
                }
            ) {
                devices[deviceName] = this
                return this as T
            }
        }

    }
}