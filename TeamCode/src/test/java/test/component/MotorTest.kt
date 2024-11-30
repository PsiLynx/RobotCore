package org.ftc3825.test.component

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Motor
import org.ftc3825.sim.timeStep
import org.ftc3825.util.assertEqual
import org.ftc3825.util.assertWithin
import org.ftc3825.util.graph.*
import org.ftc3825.util.pid.PIDFGParameters
import org.junit.Test

class MotorTest: TestClass() {

    @Test fun testRTP(){
        val motor = Motor(
            "RTPTestMotor",
            rpm=435,
            controllerParameters = PIDFGParameters(
                P=0.0006,
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
            if(i % 400 == 0) {
                graph.printLine()
            }
        }
        assertWithin(
            motor.position - 1000.0,
            epsilon = 30
        )
    }
    @Test fun testSetPower(){
        val motor = Motor("test hardwareDevice for component test", 435)
        motor.setPower(1.0)
        assertEqual(1.0, motor.lastWrite ?: 0.0)

    }
    @Test fun testSetDirection(){
        val motor = Motor("test hardwareDevice for component test", 435)
        motor.direction = Motor.Direction.REVERSE
        motor.setPower(0.5)
        assertEqual(-0.5, motor.lastWrite ?: 0.0)

    }
}
