package test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.DriveCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeLocalizer
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import org.ftc3825.util.TestClass
import org.ftc3825.util.flMotorName
import org.junit.Test
import kotlin.math.abs

class DrivetrainTest: TestClass() {
    var motor = hardwareMap.get(DcMotor::class.java, flMotorName)
    @Test fun testWeightedDrivePowers() {

        Drivetrain.reset()

        Drivetrain.setWeightedDrivePower(Pose2D(1, 0, 0))
        assert(abs(motor.power) > 0.9)
    }

    @Test fun testDriveInDirection() {
        Drivetrain.position = Pose2D()

        Drivetrain.reset()
        DriveCommand(DriveCommand.Direction.FORWARD, 24.0).schedule()
        repeat(1000) {
            CommandScheduler.update()
        }
        assertGreater(Drivetrain.position.mag, 20)

    }
}
