package test.component

import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.junit.Test

class CRServoTest: TestClass() {
   val test = HWQue.managed(CRServo(
       "test servo",
       HardwareMap.DeviceTimes.chubServo,
       FORWARD,
       1.0,
       1.0,
       Servo.Range.Default
   ))

    @Test fun testSetPower(){

        test.power = 1.0
        println(test.priority)
        HWQue.writeAll()
        assertEqual(test.power, 1.0)

        test.power = -1.0
        HWQue.writeAll()
        assertEqual(test.power, -1.0)
    }

    @Test fun testSetDirection(){
        HWQue.loopStartFun()
        test.direction = REVERSE
        test.power = -1.0

        HWQue.writeAll()
        assertEqual(
            test.hardwareDevice.position,
            1.0
        )
    }

}