package org.firstinspires.ftc.teamcode.test.FakeHardware

import org.firstinspires.ftc.teamcode.component.Encoder
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertWithin
import org.firstinspires.ftc.teamcode.util.rotations
import org.junit.Test

class EncoderTest: TestClass() {

    var motor = FakeMotor()
    var encoder = Encoder(motor, 8192.0, 2.0, 1.0)

    @Test fun testSetAngle(){
        for( i in 1..1000){
            val angle:Double = i.toDouble() / 1000.0
            encoder.angle = angle
            //System.out.println(encoder.angle)
            assertWithin(
                (encoder.angle - angle) % rotations(1),
                1e-6
            )
        }
    }
    @Test fun testSetDist(){
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
    @Test fun testWithMotorMoving(){
        for( i in 1..1000){
            motor.currentPosition = i
            encoder.update()
            val dist = i / encoder.ticksPerRevolution * encoder.wheelRadius * rotations(1)
            assertWithin(
                (encoder.distance - dist) / encoder.wheelRadius,
                1e-6)
        }
    }
}