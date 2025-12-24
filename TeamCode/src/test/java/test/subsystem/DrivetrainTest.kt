package test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.millis
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.LogTable
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import test.ShadowAppUtil
import kotlin.math.PI
import kotlin.math.abs

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class TankDrivetrainTest: TestClass() {
    @Test fun testWeightedDrivePowers() {

        TankDrivetrain.reset()
        val motor = FakeHardwareMap.get(DcMotor::class.java, "m0") as FakeMotor

        repeat(40) {
            TankDrivetrain.setWeightedDrivePower(1.0, 0.0, 0.0)
        }
        assertGreater(abs(motor.speed), 0.5)
    }
}
