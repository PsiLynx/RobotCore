package test.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.HWManager
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.sim.SimConstants.timeStep
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.control.PIDFController
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.junit.Test

class MotorTest: TestClass() {

    @Test fun testRTP(){
        val motor = HWManager.motor(
            "RTPTestMotor",
            435,
        )
        val controller = PIDFController(
            P=0.05,
            D=9.0,
            pos = { motor.position },
            apply = {
                motor.power = it
            },
            //setpointError = { targetPosition - pos() }
        )
        motor.useInternalEncoder()
        controller.targetPosition = 100.0
        (motor.hardwareDevice as FakeMotor).apply {
            maxVelocityInTicksPerSecond = 10000
            maxAccel = 4
        }

        val graph = Graph(
            Function({motor.position}, '*'),
            Function({100.0}, '|'),
            min = 0.0,
            max = 160.0
        )

        for(i in 0..1500){
            CommandScheduler.update()
            motor.update(timeStep)
            controller.updateController(timeStep)
            if(i % 50 == 0) {
                graph.printLine()
            }
        }
        assertWithin(
            motor.position - 100.0,
            epsilon = 5
        )
    }
    @Test fun testSetPower(){
        val motor = HWManager.motor("test hardwareDevice for component test", 435)
        motor.power = 1.0
        HWManager.loopEndFun()
        assertEqual(motor.power, 1.0)

    }
    @Test fun testSetDirection(){
        val name = "test hardwareDevice for component test"
        val motor = HWManager.motor(name, 435)
        motor.direction = REVERSE
        motor.power = 0.5
        HWManager.loopEndFun()
        assertEqual(
            (
                GlobalHardwareMap.get(DcMotor::class.java, name)
                as FakeMotor
            ).power,
            -0.5,
        )

    }
}
