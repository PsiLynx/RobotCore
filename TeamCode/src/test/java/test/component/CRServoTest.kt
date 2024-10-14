package org.firstinspires.ftc.teamcode.test.component

import org.ftc3825.component.CRServo
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertEqual
import org.junit.Test

class CRServoTest: TestClass() {
   val test = CRServo("test servo")

    @Test fun testSetPower(){
       test.power = 1.0
       assertEqual(test.power, 1.0)

       test.power = -1.0
       assertEqual(test.power, -1.0)
    }

    @Test fun testSetDirection(){
        test.direction = CRServo.Direction.REVERSE
        test.power = -0.5

        assertEqual(
            hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo::class.java, "test servo").power,
            0.5
        )
    }

}