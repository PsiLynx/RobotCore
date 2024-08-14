package org.firstinspires.ftc.teamcode.test.subsystem

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
import org.ftc3825.util.graph.Function
import org.junit.Test

class SlidesTest: TestClass() {
    @Test
    fun testRTP(){
        Slides.init(hardwareMap)
        Slides.reset()

        val reference = 2500
        Slides.runToPosition(reference)

        CommandScheduler.schedule(RunCommand(Slides) { } )

        val graph = Graph(
            Function({ Slides.motor.position}, '*'),
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

        assertWithin(
            Slides.motor.position - reference,
            10
        )
    }
}