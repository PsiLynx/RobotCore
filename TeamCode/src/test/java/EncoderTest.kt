package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.component.Encoder
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class EncoderTest {
    var motor = FakeMotor()
    var encoder = Encoder(motor, 8192.0, 2.0, 1.0)

    @Test
    fun testSetAngle(){
        for( i in 1..1000){
            var angle:Double = i.toDouble() / 1000.0
            //System.out.println(angle)
            encoder.angle = angle
            //System.out.println(encoder.angle)
            assertTrue(abs(encoder.angle - angle) % (2 * Math.PI) < 1e-6)
            // encoder.angle is within 1 part per million of angle, in mod(2PI) space
        }
    }

    @Test
    fun testSetDist(){
        for( i in 1..1000){
            var dist:Double = i.toDouble() / 100.0
            //System.out.println(angle)
            encoder.distance = dist
            //System.out.println(encoder.angle)
            assertTrue(abs(encoder.distance - dist) / encoder.wheelRadius < 1e-6)
        }
    }

    @Test
    fun testMotorMoving(){
        for( i in 1..1000){
            motor.currentPosition = i
            encoder.update()
            val dist = i / encoder.ticksPerRevolution * encoder.wheelRadius * 2 * Math.PI
            //println(dist)
            //println(encoder.distance)
            assertTrue(abs(encoder.distance - dist) / encoder.wheelRadius < 1e-6)
        }
    }
}