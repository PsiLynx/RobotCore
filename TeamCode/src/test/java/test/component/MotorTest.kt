package test.component

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
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

    @Test fun testSetPower(){
        val motor = Motor(
            { FakeHardwareMap.get(
                DcMotor::class.java,
                "test hardwareDevice",
            ) },
            0,
            Component.Direction.FORWARD,
        )
        motor.power = 1.0
        assertEqual(motor.power, 1.0)

    }
    @Test fun testSetDirection(){
        val name = "test hardwareDevice for component test"
        val wrapper = HardwareMap.hardwareMap?.get(
            DcMotor::class.java, name
        ) as MotorWrapper

        val motor = Motor(
            { wrapper },
            0,
            Component.Direction.FORWARD,
        )
        motor.direction = REVERSE
        motor.power = 0.5
        wrapper.toLog(LogTable.clone(Logger.getEntry()))
        assertEqual(
            wrapper.power,
            -0.5,
        )

    }
}
