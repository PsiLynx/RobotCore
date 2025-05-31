package test.component

import org.firstinspires.ftc.teamcode.component.AnalogEncoder
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeAnalogInput
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HWManager.qued
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Test
import kotlin.math.PI

class AnalogEncoderTest: TestClass() {
    @Test fun testAngle() {
        val maxVoltage = 3.25
        var encoder = AnalogEncoder(
            0,
            maxVoltage,
            0.0
        )
        HWManager.BulkData.analog[0] = maxVoltage / 2
        encoder.update(0.1)
        assertEqual(PI, encoder.angle)

        encoder = AnalogEncoder(
            0,
            maxVoltage,
            zeroVoltage = maxVoltage / 2
        )

        HWManager.BulkData.analog[0] = maxVoltage * 3.0 / 4
        encoder.update(0.1)
        println(encoder.posSupplier.asDouble)
        assertEqual(PI / 2, encoder.angle)

        HWManager.BulkData.analog[0] = maxVoltage / 4
        encoder.update(0.1)
        assertEqual(3.0 / 2, encoder.angle / PI)

        encoder = AnalogEncoder(
            0,
            maxVoltage,
            zeroVoltage = 2.205
        )

        HWManager.BulkData.analog[0] = maxVoltage / 5 + 2.205
        encoder.update(0.1)
        println(encoder.posSupplier.asDouble)
        assertWithin(PI * 2 / 5 - encoder.angle, epsilon = 1e-6)

        HWManager.BulkData.analog[0] = maxVoltage / 4 + 2.205
        encoder.update(0.1)
        assertEqual(PI / 2, encoder.angle)

        HWManager.BulkData.analog[0] = 0.0
    }
    @Test fun testMotor() {
        val motor = Motor(
            FakeMotor(),
            "analog encoder test motor",
            0,
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ).qued()
        motor.encoder = AnalogEncoder(
            0,
            3.0,
            1.5
        )
        HWManager.BulkData.analog[0] = 3.0 * 3.0/4
        motor.update(0.1)
        assertEqual(motor.angle, PI / 2)
        HWManager.BulkData.analog[0] = 0.0
    }
}