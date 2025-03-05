package test.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.teamcode.command.internal.CommandScheduler
import org.teamcode.command.internal.GlobalHardwareMap
import org.teamcode.component.Component.Direction.REVERSE
import org.teamcode.component.Motor
import org.teamcode.fakehardware.FakeMotor
import org.teamcode.sim.SimConstants.timeStep
import org.teamcode.sim.TestClass
import org.teamcode.util.graph.Function
import org.teamcode.util.graph.Graph
import org.teamcode.util.control.PIDFGParameters
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
        motor.runToPosition(1000.0, false)

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
