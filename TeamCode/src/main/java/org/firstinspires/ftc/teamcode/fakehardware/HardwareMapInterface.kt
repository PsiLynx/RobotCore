package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.util.Globals

interface HardwareMapInterface {
    var lastTime: Double
    var devices: MutableMap<String, FakeHardware>

    fun <T : Any?> get(classOrInterface: Class<out T>?, deviceName: String?): T

    fun updateDevices() {
        val deltaTime = Globals.timeSinceStart - lastTime

        devices.values.forEach { it.update(deltaTime) }

        lastTime = Globals.timeSinceStart

    }
}