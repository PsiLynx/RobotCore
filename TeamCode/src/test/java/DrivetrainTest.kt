package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertWithin
import org.junit.Test

class DrivetrainTest: TestClass() {
    var motor = hardwareMap.get(DcMotor::class.java, "frontLeft")
    @Test fun testWeightedDrivePowers() {
        Drivetrain.init(hardwareMap)

        Drivetrain.setWeightedDrivePower(Pose2D(1, 0, 0))
        assertWithin(
            motor.power - 1,
            1e-6)
    }
}