package test.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HWManager.qued
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.LogTable
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import test.ShadowAppUtil

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class MotorTest: TestClass() {

    @Test fun testRTP(){
        val motor = Motor(
            { FakeHardwareMap.get(DcMotor::class.java, "RTPTestMotor") },
            0,
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ).qued()
        val controller = PIDFController(
            P=0.1,
            D=9.0,
            pos = { motor.position },
            apply = {
                motor.power = it
            },
            setpointError = { targetPosition - pos() }
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

        val sub = object : Subsystem<Subsystem.DummySubsystem>() {
            override val components = arrayListOf(motor)

            override fun update(deltaTime: Double) {
                controller.updateController(deltaTime)
                motor.ioOp()
            }
        }

        ( sub.justUpdate() withDescription {""} ).schedule()
        for(i in 0..1500){
            CommandScheduler.update()
            (motor.hardwareDevice as FakeMotor).update(CommandScheduler.deltaTime)

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
        val motor = Motor(
            { FakeHardwareMap.get(
                DcMotor::class.java,
                "test hardwareDevice",
            ) },
            0,
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ).qued()
        motor.power = 1.0
        HWManager.writeAll()
        assertEqual(motor.power, 1.0)

    }
    @Test fun testSetDirection(){
        val name = "test hardwareDevice for component test"
        val fakeMotor =
            {
                HardwareMap.hardwareMap?.get(
                    DcMotor::class.java, name
                ) as MotorWrapper
            }

        val motor = Motor(
            fakeMotor,
            0,
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ).qued()
        motor.direction = REVERSE
        motor.power = 0.5
        HWManager.writeAll()
        fakeMotor().toLog(LogTable.clone(Logger.getEntry()))
        assertEqual(
            fakeMotor().power,
            -0.5,
        )

    }
}
