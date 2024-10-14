package org.firstinspires.ftc.teamcode.test.subsystem

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.util.Slides
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertWithin
import org.ftc3825.util.graph.Graph
import org.ftc3825.util.graph.Function
import org.junit.Test

class SlidesTest: TestClass() {
    @Test
    fun testRTP(){
        var done = false
        Slides.reset()

        val reference = 2500
        Slides.runToPosition(reference)

        (Slides.justUpdate() until {done}).schedule()

        val graph = Graph(
            Function({ Slides.motor.position}, '*'),
            Function({reference.toDouble()}, '|'),
            Function({ Slides.motor.error}, 'E'),
            min = 0.0,
            max = reference * 1.6
        )

        for(i in 0..1000){
            CommandScheduler.update()

            if(i % 50 == 0){
                graph.printLine()
            }
        }
        done = true

        assertWithin(
            Slides.motor.position - reference,
            30
        )
    }
}
