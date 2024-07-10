package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.junit.Test

class FakeMotorTest {
    @Test
    fun testSpeed() {
        val hardwareMap = FakeHardwareMap()
        var motor = hardwareMap.get(DcMotor::class.java, "motor")

        motor.power = 1.0

        for(i in 0..100){
            hardwareMap.updateDevices()
            sleep(0.01)
        }
        assert((motor as FakeMotor).speed > 0.5)
    }
}