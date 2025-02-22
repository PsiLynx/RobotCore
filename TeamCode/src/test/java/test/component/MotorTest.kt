package test.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.fakehardware.FakeMotor
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
                P=0.006,
                D=0.07,
            )
        )
        motor.useInternalEncoder()
        motor.pos = { motor.encoder!!.distance }
        motor.setpointError = { motor.setpoint - motor.pos() }
        motor.runToPosition(1000.0)

        val graph = Graph(
            Function({motor.ticks}, '*'),
            Function({1000.0}, '|'),
            min = 0.0,
            max = 1600.0
        )

        for(i in 0..300){
            CommandScheduler.update()
            motor.update(timeStep)
            if(i % 10 == 0) {
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
        motor.power = 1.0
        assertEqual(1.0, (motor.lastWrite or 0.0) )

    }
    @Test fun testSetDirection(){
        val name = "test hardwareDevice for component test"
        val motor = Motor(name, 435)
        motor.direction = REVERSE
        motor.power = 0.5
        assertEqual(
            (
                GlobalHardwareMap.get(DcMotor::class.java, name)
                as FakeMotor
            ).power,
            -0.5,
        )

    }
}
