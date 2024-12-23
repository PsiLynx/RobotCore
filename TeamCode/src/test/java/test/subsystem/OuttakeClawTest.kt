package test.subsystem

import com.qualcomm.robotcore.hardware.Servo
import org.junit.Test
import org.ftc3825.util.Claw
import org.ftc3825.util.TestClass

class OuttakeClawTest: TestClass() {

    @Test fun stateMachineTest() {
        Claw.reset()

        val servo = hardwareMap.get(Servo::class.java, "clawServo")

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
