package test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import junit.framework.TestCase.assertEquals
import org.firstinspires.ftc.robotcore.external.navigation.Velocity
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.fakehardware.FakeOctoQuad
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.millis
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.core.TermCriteria.EPS
import org.psilynx.psikit.core.LogTable
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import test.ShadowAppUtil
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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
    @Test fun testFuturePose(){
        fun test(start: Pose2D, velocity: Pose2D, dt: Double, expected: Pose2D) {
            val result = TankDrivetrain.futurePos(
                dt,
                start, velocity
            )

            assertEquals(expected.x, result.x, 1e-4)
            assertEquals(expected.y, result.y, 1e-4)
            assertEquals(
                expected.heading.toDouble(),
                result.heading.toDouble(),
                1e-4
            )
        }
        test(
            Pose2D(), Pose2D(1, 0, 0), 1.0, Pose2D(1, 0, 0)
        )
        test(
            Pose2D(), Pose2D(0, 0, 1), 1.0, Pose2D(0, 0, 1)
        )
        test(
            Pose2D(), Pose2D(1, 0, 1), 1.0, Pose2D(sin(1.0), 1 - cos(1.0), 1)
        )
    }
}
