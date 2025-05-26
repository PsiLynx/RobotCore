package test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.millis
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs

class DrivetrainTest: TestClass() {
    @Test fun testWeightedDrivePowers() {

        Drivetrain.reset()
        val motor = HardwareMap.frontLeft(Component.Direction.FORWARD)

        Drivetrain.setWeightedDrivePower(1.0, 0.0, 0.0)
        repeat(4) {
            HWQue.loopStartFun()
            Drivetrain.setWeightedDrivePower(1.0, 0.0, 0.0)
            HWQue.loopEndFun()
        }
        assertGreater(abs((motor.hardwareDevice as FakeMotor).power), 0.9)
    }
    @Test fun testResetPos() {
        test(4.0)
        CommandScheduler.end()
        println("test reset pos")
        Drivetrain.resetToCorner(InstantCommand {}).schedule()
        repeat(10) { CommandScheduler.update() }
        Drivetrain.run { dt ->
            println(dt.position)
            dt.driveFieldCentric(Pose2D(1.0, 0.0, 0.0))
        }.schedule()
        repeat(50) {
            CommandScheduler.update()
        }
        assertGreater(Drivetrain.position.x - Drivetrain.cornerPos.x, 10)
        assertGreater(
            Drivetrain.position.x - Drivetrain.cornerPos.x,
            Drivetrain.position.y - Drivetrain.cornerPos.y
        )

    }
    @Test fun testDriveFieldCentric() {
        test(0.0)
        test(PI / 2)
        test(1.0)
        test(0.1)
    }

    fun test(heading: Double) {
        CommandScheduler.end()
        Drivetrain.reset()
        Drivetrain.motors.forEach {
            it.hardwareDevice.resetDeviceConfigurationForOpMode()
        }
        Drivetrain.position = Pose2D(0, 0, heading)

        Drivetrain.run { dt ->
            println(dt.position)
            dt.driveFieldCentric(Pose2D(1.0, 0.0, 0.0))
        }.schedule()
        HWQue.minimumLooptime = millis(40)
        repeat(300) {
            CommandScheduler.update()
        }
        HWQue.minimumLooptime = millis(0)
        assertGreater(Drivetrain.position.x, 10)
        assertGreater(Drivetrain.position.x, Drivetrain.position.y)


        CommandScheduler.reset()
        Drivetrain.reset()
        Drivetrain.position = Pose2D(0, 0, heading)

        Drivetrain.run { dt ->
            println(dt.position)
            dt.driveFieldCentric(Pose2D(0.0, 1.0, 0.0))
        }.schedule()
        repeat(300) {
            CommandScheduler.update()
        }
        assertGreater(Drivetrain.position.y, 10)
        assertGreater(Drivetrain.position.y, Drivetrain.position.x)
    }
}
