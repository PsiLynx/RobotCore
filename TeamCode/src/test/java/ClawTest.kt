package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Claw
import org.firstinspires.ftc.teamcode.subsystem.Claw.transition.close
import org.firstinspires.ftc.teamcode.subsystem.Claw.transition.open
import org.junit.Test

class ClawTest {
    var hardwareMap = FakeHardwareMap()
    var servo = hardwareMap.get(Servo::class.java, "clawServo")
    @Test
    fun stateMachineTest() {
        Claw.transitionTo(close)
        assertEqual(servo.position, 0.0)

        Claw.transitionTo(open)
        print("")
        assertEqual(servo.position, 1.0)

        Claw.transitionTo(open)
        assertEqual(servo.position, 1.0)

        Claw.transitionTo(close)
        assertEqual(servo.position, 0.0)
    }
}