package test.component

import org.firstinspires.ftc.teamcode.component.AnalogEncoder
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeAnalogInput
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Test
import kotlin.jvm.Throws
import kotlin.math.PI

class AnalogEncoderTest: TestClass() {
    @Test fun testAngle() {
        val maxVoltage = 3.25
        var encoder = AnalogEncoder("test analog encoder", maxVoltage, 0.0)
        var hardwareDevice = encoder.hardwareDevice as FakeAnalogInput

        hardwareDevice.voltage = maxVoltage / 2
        encoder.update(0.1)
        assertEqual(PI, encoder.angle)

        encoder = AnalogEncoder(
            "test analog encoder 2",
            maxVoltage,
            zeroVoltage = maxVoltage / 2
        )
        hardwareDevice = encoder.hardwareDevice as FakeAnalogInput

        hardwareDevice.voltage = maxVoltage * 3.0 / 4
        encoder.update(0.1)
        println(encoder.posSupplier.asDouble)
        assertEqual(PI / 2, encoder.angle)

        hardwareDevice.voltage = maxVoltage / 4
        encoder.update(0.1)
        assertEqual(3.0 / 2, encoder.angle / PI)

        encoder = AnalogEncoder(
            "test analog encoder 3",
            maxVoltage,
            zeroVoltage = 2.205
        )
        hardwareDevice = encoder.hardwareDevice as FakeAnalogInput

        hardwareDevice.voltage = maxVoltage / 5 + 2.205
        encoder.update(0.1)
        println(encoder.posSupplier.asDouble)
        assertEqual(PI * 2 / 5, encoder.angle)

        hardwareDevice.voltage = maxVoltage / 4 + 2.205
        encoder.update(0.1)
        assertEqual(PI / 2, encoder.angle)
    }
    @Test fun testMotor() {
        val motor = Motor("analog encoder test motor", 435)
        motor.encoder = AnalogEncoder("analog motor test encoder", 3.0, 1.5)
        ( (motor.encoder!! as AnalogEncoder).hardwareDevice as FakeAnalogInput )
            .voltage = 3.0 * 3.0/4
        motor.update(0.1)
        println( (motor.encoder!! as AnalogEncoder).hardwareDevice.voltage )
        assertEqual(motor.angle, PI / 2)
    }
}