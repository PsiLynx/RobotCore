package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Claw.Transition.Close
import org.ftc3825.subsystem.Claw.Transition.Open
import org.ftc3825.subsystem.Slides
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertEqual
import org.ftc3825.util.assertWithin
import org.ftc3825.util.graph.Graph
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

    @Test
    fun testSlidesRTP(){
        Slides.init(hardwareMap)

        val reference = 2500
        Slides.runToPosition(reference)

        CommandScheduler.schedule(RunCommand(Slides) { } )

        val graph = Graph(
            Function({Slides.motor.position}, '*'),
            Function({reference.toDouble()}, '|'),
            //Function({sqrt(abs(Slides.slideMotor.accumulatedError))}, '+'),
            min = 0.0,
            max = reference * 1.6
        )

        for(i in 0..1000){
            CommandScheduler.update()

            if(i % 10 == 0){
                graph.printLine()
            }
        }

        assertWithin(Slides.motor.position - reference,
            10
        )
    }
}