package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.VoltageSensor
import com.qualcomm.robotcore.hardware.CRServo

object FakeHardwareMap : JVMHardwareMap() {
    override var deviceTypes:
            MutableMap<Class<out Any>, (String) -> HardwareDevice> =
        mutableMapOf(
            IMU::class.java to { FakeIMU() },
            Servo::class.java to { FakeServo() },
            DcMotor::class.java to { FakeMotor() },
            CRServo::class.java to { FakeCRServo() },
            Gamepad::class.java to { FakeGamepad() },
            VoltageSensor::class.java to { FakeVoltageSensor() },
        )

}
