package test.subsystem

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
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
class DrivetrainTest: TestClass() {
    @Test fun testWeightedDrivePowers() {

        Drivetrain.reset()
        val motor = HardwareMap.frontLeft(Component.Direction.FORWARD).hardwareDevice
                as MotorWrapper

        repeat(4) {
            Drivetrain.setWeightedDrivePower(1.0, 0.0, 0.0)
            motor.toLog(LogTable.clone(Logger.getEntry()))
        }
        assertGreater(abs(motor.power), 0.9)
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
        repeat(70) {
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
        repeat(300) {
            CommandScheduler.update()
        }
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
