package test.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.util.control.PIDFController
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.firstinspires.ftc.teamcode.util.millis
import org.junit.Test

class MotorTest: TestClass() {

    @Test fun testRTP(){
        val motor = HWQue.managed(Motor(
            "RTPTestMotor",
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ))
        val controller = PIDFController(
            P=0.1,
            D=9.0,
            pos = { motor.position },
            apply = {
                motor.power = it
            },
            //setpointError = { targetPosition - pos() }
        )
        motor.useInternalEncoder(500.0, 1.0)
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

        val Sub = object : Subsystem<Subsystem.DummySubsystem>() {
            override val components = arrayListOf(motor)

            override fun update(deltaTime: Double) {
                controller.updateController(deltaTime)
                motor.ioOp()
            }
        }

        ( Sub.justUpdate() withDescription {""} ).schedule()
        HWQue.minimumLooptime = millis(20)
        for(i in 0..1500){
            CommandScheduler.update()
            if(i % 50 == 0) {
                graph.printLine()
            }
        }
        HWQue.minimumLooptime = millis(0)
        assertWithin(
            motor.position - 100.0,
            epsilon = 5
        )
    }
    @Test fun testSetPower(){
        val motor = HWQue.managed(Motor(
            "test hardwareDevice for component test",
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ))
        motor.power = 1.0
        HWQue.writeAll()
        assertEqual(motor.power, 1.0)

    }
    @Test fun testSetDirection(){
        val name = "test hardwareDevice for component test"
        val motor = HWQue.managed(Motor(
            name,
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ))
        motor.direction = REVERSE
        motor.power = 0.5
        HWQue.writeAll()
        assertEqual(
            (
                HardwareMap.get(DcMotor::class.java, name)
                as FakeMotor
            ).power,
            -0.5,
        )

    }
}
