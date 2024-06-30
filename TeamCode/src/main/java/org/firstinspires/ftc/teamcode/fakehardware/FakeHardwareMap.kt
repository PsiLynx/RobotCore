package org.firstinspires.ftc.teamcode.fakehardware

import android.content.Context
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.IMU

class FakeHardwareMap(appContext: Context?,
                      notifier: OpModeManagerNotifier?
) : com.qualcomm.robotcore.hardware.HardwareMap(appContext, notifier) {
    constructor() : this(null, null)

    override fun <T : Any?> get(classOrInterface: Class<out T>?, deviceName: String?): T {
        if (
            devices[deviceName] != null
            ) return devices[deviceName] as T
        else {
            with(
                when (classOrInterface) {
                    IMU::class.java -> FakeIMU()
                    DcMotor::class.java -> FakeMotor()
                    else -> throw IllegalArgumentException(String.format("Unable to find a hardware device with name \"%s\" and type %s", deviceName, classOrInterface?.getSimpleName()))
                }
            ) {
                devices[deviceName!!] = this
                return this as T
            }
        }

    }
    fun updateDevices() {
        devices.values.forEach() {
            it.update()
        }
    }
    companion object{
        var devices = mutableMapOf<String, FakeHardware>()
    }
}