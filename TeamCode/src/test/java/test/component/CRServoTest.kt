package test.component

import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.junit.Test

class CRServoTest: TestClass() {
   val test = HWQue.crServo(
       "test servo",
       FORWARD,
       1.0,
       1.0,
       Servo.Range.Default
   )

    @Test fun testSetPower(){

        HWQue.loopStartFun()
        test.power = 1.0
        HWQue.loopEndFun()
        assertEqual(test.power, 1.0)

        HWQue.loopStartFun()
        test.power = -1.0
        HWQue.loopEndFun()
        assertEqual(test.power, -1.0)
    }

    @Test fun testSetDirection(){
        HWQue.loopStartFun()
        test.direction = REVERSE
        test.power = -1.0

        HWQue.loopEndFun()
        assertEqual(
            hardwareMap.get(
                com.qualcomm.robotcore.hardware.Servo::class.java, "test servo"
            ).position,
            1.0
        )
    }

}