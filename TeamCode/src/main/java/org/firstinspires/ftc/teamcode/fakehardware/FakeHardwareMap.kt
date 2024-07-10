package org.firstinspires.ftc.teamcode.fakehardware

import android.content.Context
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.util.nanoseconds

class FakeHardwareMap(appContext: Context?,
                      notifier: OpModeManagerNotifier?
) : com.qualcomm.robotcore.hardware.HardwareMap(appContext, notifier) {
    var startNS = 0L

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
                    Servo::class.java -> FakeServo()
                    VoltageSensor::class.java -> FakeVoltageSensor()
                    else -> throw IllegalArgumentException(String.format("Unable to find a hardware device with name \"%s\" and type %s", deviceName, classOrInterface?.getSimpleName()))
                }
            ) {
                devices[deviceName!!] = this
                return this as T
            }
        }

    }
    fun updateDevices() {
        if(startNS == 0L){
            startNS = System.nanoTime()
        }
        val deltaTime = nanoseconds(System.nanoTime() - startNS)

        devices.values.forEach() {
            it.update(deltaTime)
        }
    }
    companion object{
        var devices = mutableMapOf<String, FakeHardware>()
    }
}