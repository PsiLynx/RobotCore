package org.firstinspires.ftc.teamcode.test.subsystem

import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Claw.Transition.Close
import org.ftc3825.subsystem.Claw.Transition.Open
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertEqual
import org.junit.Test

class ClawTest: TestClass() {

    @Test fun stateMachineTest() {
        Claw.reset()

        var servo = hardwareMap.get(Servo::class.java, "clawServo")
        Claw.init(hardwareMap)

        Claw.transitionTo(Close)
        assertEqual(servo.position, 0.0)

        Claw.transitionTo(Open)
        print("")
        assertEqual(servo.position, 1.0)

        Claw.transitionTo(Open)
        assertEqual(servo.position, 1.0)

        Claw.transitionTo(Close)
        assertEqual(servo.position, 0.0)
    }

}