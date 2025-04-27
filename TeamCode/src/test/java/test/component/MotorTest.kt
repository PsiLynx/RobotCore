package test.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.HWManager
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.sim.SimConstants.timeStep
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.firstinspires.ftc.teamcode.util.control.PIDFGParameters
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
        motor.pos = { motor.encoder!!.pos }
        motor.setpointError = { motor.setpoint - motor.pos() }
        motor.runToPosition(1000.0, false)

        val graph = Graph(
            Function({motor.ticks}, '*'),
            Function({1000.0}, '|'),
            min = 0.0,
            max = 1600.0
        )

        for(i in 0..2000){
            CommandScheduler.update()
            motor.update(timeStep)
            if(i % 50 == 0) {
                graph.printLine()
            }
        }
        assertWithin(
            motor.ticks - 1000.0,
            epsilon = 30
        )
    }
    @Test fun testSetPower(){
        val motor = HWManager.motor("test hardwareDevice for component test", 435)
        motor.power = 1.0
        assertEqual(1.0, (motor.lastWrite or 0.0) )

    }
    @Test fun testSetDirection(){
        val name = "test hardwareDevice for component test"
        val motor = HWManager.motor(name, 435)
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
