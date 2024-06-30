package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.Pose2D
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class DrivetrainTest {
    val hardwareMap = FakeHardwareMap()
    var drivetrain = Drivetrain(hardwareMap)
    var motor = hardwareMap.get(DcMotor::class.java, "frontLeft")
    @Test
    fun testWeightedDrivePowers() {
        drivetrain.setWeightedDrivePower(Pose2D(1.0, 0.0, 0.0))
        println(motor.power)
        assertTrue(abs(motor.power - 1.0) < 1e-6)
    }
}