package org.firstinspires.ftc.teamcode.test.FakeHardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Encoder
import org.ftc3825.fakehardware.FakeGamepad
import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertGreater
import org.ftc3825.util.assertWithin
import org.ftc3825.util.rotations
import org.junit.Test

class EncoderTest: TestClass() {

    var motor = FakeMotor()
    var encoder = Encoder(motor, 8192.0, 2.0, 1.0)

    @Test fun testSetAngle(){
        for( i in 1..1000){
            val angle:Double = i.toDouble() / 1000.0
            encoder.angle = angle
            assertWithin(
                (encoder.angle - angle) % rotations(1),
                1e-6
            )
        }
    }
    @Test fun testSetDist(){
        for( i in 1..1000){
            val dist = i.toDouble() / 100.0
            encoder.distance = dist
            assertWithin(
                (encoder.distance - dist),
                1e-6
            )
        }
    }
    @Test fun testWithMotorMoving(){
        for( i in 1..1000){
            motor.setCurrentPosition(i)
            encoder.update()
            val dist = i
            assertWithin(
                (encoder.distance - dist),
                1e-6)
        }
    }
}