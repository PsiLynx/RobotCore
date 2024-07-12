package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Encoder
import org.firstinspires.ftc.teamcode.fakehardware.FakeGamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.util.rotations
import org.junit.Test

class FakeHardwareTest: TestClass() {
    val hardwareMap = FakeHardwareMap()

    var motor = FakeMotor()
    var encoder = Encoder(motor, 8192.0, 2.0, 1.0)

    @Test fun testEncoderSetAngle(){
        for( i in 1..1000){
            val angle:Double = i.toDouble() / 1000.0
            encoder.angle = angle
            //System.out.println(encoder.angle)
            assertWithin(
                (encoder.angle - angle) % rotations(1),
                1e-6)
            // encoder.angle is within 1 part per million of angle, in mod(2PI) space
        }
    }
    @Test fun testEncoderSetDist(){
        for( i in 1..1000){
            val dist:Double = i.toDouble() / 100.0
            encoder.distance = dist
            //System.out.println(encoder.angle)
            assertWithin(
                (encoder.distance - dist) / encoder.wheelRadius,
                1e-6
            )
        }
    }
    @Test fun testEncoderWithMotorMoving(){
        for( i in 1..1000){
            motor.currentPosition = i
            encoder.update()
            val dist = i / encoder.ticksPerRevolution * encoder.wheelRadius * rotations(1)
            assertWithin(
                (encoder.distance - dist) / encoder.wheelRadius,
                1e-6)
        }
    }

    @Test fun testMotorSpeed() {

        val motor = hardwareMap.get(DcMotor::class.java, "motor")
        motor.resetDeviceConfigurationForOpMode()

        motor.power = 1.0
        CommandScheduler.init(hardwareMap)

        for(i in 0..100){
            CommandScheduler.update()

            println((motor as FakeMotor).speed)
        }
        val fakeMotor = motor as FakeMotor
        assertGreater(fakeMotor.speed, 0.6)
        assertGreater(1.0, fakeMotor.speed)
    }

//    @Test fun test1000x(){
//        for( i in 0..1000){
//
//            testMotorSpeed()
//        }
//    }

    @Test fun testGamepadPress() {
        val gamepad = hardwareMap.get(Gamepad::class.java, "gamepad1")

        val fakeGamepad = (gamepad as FakeGamepad)

        fakeGamepad.press( "a" )
        assert(gamepad.a == true)

        fakeGamepad.depress( "a" )
        assert(gamepad.a == false)
    }
}