package test.FakeHardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.millis
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import test.ShadowAppUtil

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class MotorTest: TestClass() {
    @Test
    fun testMotorSpeed() {

        val motor = FakeHardwareMap.get(
            DcMotor::class.java, "test speed hardwareDevice"
        )
        motor.resetDeviceConfigurationForOpMode()

        motor.power = 1.0

        for(i in 0..50){
            CommandScheduler.update()
            FakeTimer.addTime(0.01)
            FakeHardwareMap.updateDevices()
        }
        val fakeMotor = motor as FakeMotor
        assertGreater(fakeMotor.speed, 0.6)
        assertGreater(1.0, fakeMotor.speed)
    }
}
