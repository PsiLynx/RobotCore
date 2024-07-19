package org.firstinspires.ftc.teamcode.fakehardware

import android.content.Context
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.sim.SimulatedMotor
import org.firstinspires.ftc.teamcode.util.Globals

object FakeHardwareMap : JVMHardwareMap() {
    override var deviceTypes:
            MutableMap<Class<out Any>, (String) -> HardwareDevice> =
        mutableMapOf(
            IMU::class.java to { FakeIMU() },
            Servo::class.java to { FakeServo() },
            DcMotor::class.java to { FakeMotor() },
            Gamepad::class.java to { FakeGamepad() },
            VoltageSensor::class.java to { FakeVoltageSensor() }
        )

}