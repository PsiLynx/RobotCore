package org.firstinspires.ftc.teamcode.test.subsystem

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.subsystem.Claw
import org.firstinspires.ftc.teamcode.subsystem.Claw.Transition.Close
import org.firstinspires.ftc.teamcode.subsystem.Claw.Transition.Open
import org.firstinspires.ftc.teamcode.subsystem.Slides
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertEqual
import org.firstinspires.ftc.teamcode.util.assertWithin
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.junit.Test

class SubsystemTest: TestClass() {

    @Test fun stateClawMachineTest() {
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