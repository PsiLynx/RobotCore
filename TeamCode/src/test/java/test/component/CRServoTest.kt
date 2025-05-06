package test.component

import org.firstinspires.ftc.teamcode.component.CRServo
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.HWManager
import org.junit.Test

class CRServoTest: TestClass() {
   val test = HWManager.crServo("test servo", FORWARD)

    @Test fun testSetPower(){
        test.power = 1.0
        HWManager.loopEndFun()
        assertEqual(test.power, 1.0)

        test.power = -1.0
        HWManager.loopEndFun()
        assertEqual(test.power, -1.0)
    }

    @Test fun testSetDirection(){
        test.direction = REVERSE
        test.power = -0.5

        HWManager.loopEndFun()
        assertEqual(
            hardwareMap.get(
                com.qualcomm.robotcore.hardware.CRServo::class.java, "test servo"
            ).power,
            0.5
        )
    }

}