package org.ftc3825.test.subsystem

import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertEqual
import org.junit.Test
import org.ftc3825.util.Claw

class ClawTest: TestClass() {

    @Test fun stateMachineTest() {
        Claw.reset()

        var servo = hardwareMap.get(Servo::class.java, "clawServo")

        Claw.transitionTo(Claw.Transition.Close)
        assertEqual(servo.position, 0.0)

        Claw.transitionTo(Claw.Transition.Open)
        print("")
        assertEqual(servo.position, 1.0)

        Claw.transitionTo(Claw.Transition.Open)
        assertEqual(servo.position, 1.0)

        Claw.transitionTo(Claw.Transition.Close)
        assertEqual(servo.position, 0.0)
    }

}
