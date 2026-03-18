package test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import junit.framework.TestCase.assertEquals
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import test.ShadowAppUtil
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class TankDrivetrainTest: TestClass() {
    @Test fun testWeightedDrivePowers() {

        TankDrivetrain.reset()
        val motor = FakeMotor.fromDcMotor(
            TankDrivetrain.motors.first().hardwareDevice as DcMotor
        )

        repeat(40) {
            TankDrivetrain.setWeightedDrivePower(1.0, 0.0, 0.0)
        }
        assertGreater(abs(motor.power), 0.5)
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
