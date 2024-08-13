package org.firstinspires.ftc.teamcode.test.subsystem

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.subsystem.Slides
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertWithin
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.junit.Test

class SlidesTest: TestClass() {
    @Test
    fun testRTP(){
        Slides.init(hardwareMap)

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