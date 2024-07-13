package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Claw
import org.firstinspires.ftc.teamcode.subsystem.Claw.Transition.Close
import org.firstinspires.ftc.teamcode.subsystem.Claw.Transition.Open
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertEqual
import org.junit.Test

class ClawTest: TestClass() {
    var servo = hardwareMap.get(Servo::class.java, "clawServo")

    @Test fun stateMachineTest() {
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