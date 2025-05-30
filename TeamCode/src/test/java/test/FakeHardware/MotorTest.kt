package test.FakeHardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.millis
import org.junit.Test

class MotorTest: TestClass() {
    @Test
    fun testMotorSpeed() {

        val motor = hardwareMap.get(DcMotor::class.java, "test speed hardwareDevice")
        motor.resetDeviceConfigurationForOpMode()

        motor.power = 1.0

        HWManager.minimumLooptime = millis(20)
        for(i in 0..40){
            CommandScheduler.update()
        }
        HWManager.minimumLooptime = millis(0)
        val fakeMotor = motor as FakeMotor
        assertGreater(fakeMotor.speed, 0.6)
        assertGreater(1.0, fakeMotor.speed)
    }
}
