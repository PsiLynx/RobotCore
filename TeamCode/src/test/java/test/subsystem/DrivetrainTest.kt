package org.firstinspires.ftc.teamcode.test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.DriveCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeLocalizer
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertWithin
import org.ftc3825.util.frMotorName
import org.junit.Test

class DrivetrainTest: TestClass() {
    var motor = hardwareMap.get(DcMotor::class.java, "frontLeft")
    @Test fun testWeightedDrivePowers() {

        Drivetrain.init(hardwareMap)
        Drivetrain.reset()

        Drivetrain.setWeightedDrivePower(Pose2D(1, 0, 0))
        assertWithin(
            motor.power - 1,
            1e-6)
    }

    @Test fun testDriveInDirection() {
        Drivetrain.init(hardwareMap)
        val localizer = FakeLocalizer(hardwareMap)

        Drivetrain.reset()
        CommandScheduler.schedule( DriveCommand(localizer, DriveCommand.Direction.FORWARD, 24.0))
        repeat(1000) {
            CommandScheduler.update()
        }

    }
}