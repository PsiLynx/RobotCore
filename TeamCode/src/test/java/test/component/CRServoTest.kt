package test.component

import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.fakehardware.FakeServo
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import test.ShadowAppUtil

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class CRServoTest: TestClass() {
   val test = CRServo(
       { FakeServo() },
       0,
       FORWARD,
       Servo.Range.Default
   )

    @Test fun testSetPower(){

        test.direction = FORWARD

        test.power = 1.0
        assertEqual(test.power, 1.0)

        test.power = -1.0
        assertEqual(test.power, -1.0)
    }

    @Test fun testSetDirection(){
        test.direction = REVERSE
        test.power = -1.0

        assertEqual(
            (test.hardwareDevice as ServoImplEx).position,
            1.0
        )
    }

}