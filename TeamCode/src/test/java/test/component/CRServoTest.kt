package test.component

import org.teamcode.component.CRServo
import org.teamcode.component.Component.Direction.FORWARD
import org.teamcode.sim.TestClass
import org.teamcode.component.Component.Direction.REVERSE
import org.junit.Test

class CRServoTest: TestClass() {
   val test = CRServo("test servo", FORWARD)

    @Test fun testSetPower(){
       test.power = 1.0
       assertEqual(test.power, 1.0)

       test.power = -1.0
       assertEqual(test.power, -1.0)
    }

    @Test fun testSetDirection(){
        test.direction = REVERSE
        test.power = -0.5

        assertEqual(
            hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo::class.java, "test servo").power,
            0.5
        )
    }

}