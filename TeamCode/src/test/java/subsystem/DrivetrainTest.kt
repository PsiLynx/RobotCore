package org.firstinspires.ftc.teamcode.test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertWithin
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