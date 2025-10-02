package test.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.AnalogEncoder
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeAnalogInput
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HWManager.qued
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import test.ShadowAppUtil
import kotlin.math.PI

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class AnalogEncoderTest: TestClass() {
    @Test fun testAngle() {
        val maxVoltage = 3.25
        val input = FakeAnalogInput()
        var encoder = AnalogEncoder(
            { input },
            maxVoltage,
            0.0
        )
        input.setVoltage( maxVoltage / 2)
        encoder.update(0.1)
        assertEqual(PI, encoder.angle)

        encoder = AnalogEncoder(
            { input },
            maxVoltage,
            zeroVoltage = maxVoltage / 2
        )

        input.voltage = maxVoltage * 3.0 / 4
        encoder.update(0.1)
        println(encoder.posSupplier.asDouble)
        assertEqual(PI / 2, encoder.angle)

        input.voltage = maxVoltage / 4
        encoder.update(0.1)
        assertEqual(3.0 / 2, encoder.angle / PI)

        encoder = AnalogEncoder(
            { input },
            maxVoltage,
            zeroVoltage = 2.205
        )

        input.voltage = maxVoltage / 5 + 2.205
        encoder.update(0.1)
        println(encoder.posSupplier.asDouble)
        assertWithin(PI * 2 / 5 - encoder.angle, epsilon = 1e-6)

        input.voltage = maxVoltage / 4 + 2.205
        encoder.update(0.1)
        assertEqual(PI / 2, encoder.angle)

        input.voltage = 0.0
    }
    @Test fun testMotor() {
        val motor = Motor(
            {
                FakeHardwareMap.get(
                    DcMotor::class.java, "analog encoder test motor"
                )
            },
            0,
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ).qued()
        val input = FakeAnalogInput()
        motor.encoder = AnalogEncoder(
            { input },
            3.0,
            1.5
        )
        input.voltage = 3.0 * 3.0/4
        motor.update(0.1)
        assertEqual(motor.angle, PI / 2)
        input.voltage = 0.0
    }
}