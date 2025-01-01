package test.component

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.sim.timeStep
import org.ftc3825.util.TestClass
import org.ftc3825.util.graph.Function
import org.ftc3825.util.graph.Graph
import org.ftc3825.util.pid.PIDFGParameters
import org.junit.Test

class MotorTest: TestClass() {

    @Test fun testRTP(){
        val motor = Motor(
            "RTPTestMotor",
            rpm=435,
            controllerParameters = PIDFGParameters(
                P=0.6,
                I=0.0000025,
                D=0.01,
                F=0,
                G=0
            )
        )
        motor.useInternalEncoder()
        motor.runToPosition(1000.0)

        val graph = Graph(
            Function({motor.ticks}, '*'),
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
            motor.ticks - 1000.0,
            epsilon = 30
        )
    }
    @Test fun testSetPower(){
        val motor = Motor("test hardwareDevice for component test", 435)
        motor.setPower(1.0)
        assertEqual(1.0, (motor.lastWrite or 0.0) )

    }
    @Test fun testSetDirection(){
        val motor = Motor("test hardwareDevice for component test", 435)
        motor.direction = REVERSE
        motor.setPower(0.5)
        assertEqual(-0.5, (motor.lastWrite or 0.0) )

    }
}
