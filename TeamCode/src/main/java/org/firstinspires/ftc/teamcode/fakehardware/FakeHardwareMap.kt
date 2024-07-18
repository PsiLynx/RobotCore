package org.firstinspires.ftc.teamcode.fakehardware

import android.content.Context
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.sim.SimulatedMotor
import org.firstinspires.ftc.teamcode.util.Globals

object FakeHardwareMap : HardwareMap(null, null) {

    var lastTime = Globals.timeSinceStart
    var devices = mutableMapOf<String, FakeHardware>()

    override fun <T : Any?> get(classOrInterface: Class<out T>?, deviceName: String?): T {
        if(deviceName == null) throw IllegalArgumentException("device name cannot be null in fakeHardwareMap.get()")
        if (
            devices[deviceName] != null
            ) return devices[deviceName] as T
        else {
            with(
                when (classOrInterface) {
                    IMU::class.java -> FakeIMU()
                    Servo::class.java -> FakeServo()
                    DcMotor::class.java -> FakeMotor()
                    Gamepad::class.java -> FakeGamepad()
                    VoltageSensor::class.java -> FakeVoltageSensor()
                    else -> throw IllegalArgumentException(
                        String.format(
                            "Unable to find a hardware device with name \"%s\" and type %s",
                            deviceName,
                            classOrInterface?.getSimpleName()
                        )
                    )
                }
            ) {
                devices[deviceName] = this
                return this as T
            }
        }

    }
    fun updateDevices() {
        val deltaTime = Globals.timeSinceStart - lastTime

        devices.values.forEach { it.update(deltaTime) }

        lastTime = Globals.timeSinceStart

    }
}