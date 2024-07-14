package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.sim.timeStep
import org.firstinspires.ftc.teamcode.util.PIDFGParameters
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertWithin
import org.firstinspires.ftc.teamcode.util.graph.*
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.junit.Test

class ComponentTest: TestClass() {

    @Test fun testMotorRTP(){
        val motor = Motor(
            "RTPTestMotor",
            hardwareMap,
            rpm=435,
            controllerParameters = PIDFGParameters(
                P=0.00011,
                I=0.0000025,
                D=0.01,
                F=0,
                G=0
            )
        )
        motor.useInternalEncoder()
        motor.runToPosition(1000.0)

        val graph = Graph(
            Function({motor.position}, '*'),
            Function({1000.0}, '|'),
            min = 0.0,
            max = 1600.0
        )

        for(i in 0..5000){
            CommandScheduler.update()
            motor.update(timeStep)
            if(i % 10 == 0) {
                graph.printLine()
            }
        }
        assertWithin(
            motor.position - 1000.0,
            epsilon = 10
        )
    }
}