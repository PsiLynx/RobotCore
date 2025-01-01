package test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
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
}
