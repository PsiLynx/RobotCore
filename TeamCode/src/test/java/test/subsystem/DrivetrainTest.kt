package test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.HWManager
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.flMotorName
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs

class DrivetrainTest: TestClass() {
    @Test fun testWeightedDrivePowers() {

        Drivetrain.reset()
        val motor = hardwareMap.get(DcMotor::class.java, flMotorName)

        Drivetrain.setWeightedDrivePower(1.0, 0.0, 0.0)
        HWManager.update()
        assert(abs(motor.power) > 0.9)
    }
    @Test fun testDriveFieldCentric() {

        fun test(heading: Double) {
            CommandScheduler.reset()
            Drivetrain.reset()
            Drivetrain.position = Pose2D(0, 0, heading)

            Drivetrain.run { dt ->
                println(dt.position)
                dt.motors.forEach {
                    println("${it.name}: ${it.lastWrite}")

                }
                dt.driveFieldCentric(Pose2D(1.0, 0.0, 0.0))
            }.schedule()
            repeat(50) {
                CommandScheduler.update()
            }
            assertGreater(Drivetrain.position.x, 10)
            assertGreater(Drivetrain.position.x, Drivetrain.position.y)


            CommandScheduler.reset()
            Drivetrain.reset()
            Drivetrain.position = Pose2D(0, 0, heading)
            //println(Drivetrain.position)

            Drivetrain.run { dt ->
                println(dt.position)
                dt.motors.forEach {
                    println("${it.name}: ${it.velocity}")
                }
                dt.driveFieldCentric(Pose2D(0.0, 1.0, 0.0))
            }.schedule()
            repeat(50) {
                CommandScheduler.update()
            }
            assertGreater(Drivetrain.position.y, 10)
            assertGreater(Drivetrain.position.y, Drivetrain.position.x)
//
//
//
//            Drivetrain.reset()
//            CommandScheduler.reset()
//            Drivetrain.position = Pose2D(0, 0, heading)
//            //println(Drivetrain.position)
//
//            Drivetrain.run {
//                it.driveFieldCentric(Pose2D(0.0, 0.0, 1.0))
//            }.schedule()
//            repeat(50) {
//                CommandScheduler.update()
//            }
//            assertGreater(Drivetrain.velocity.heading.toDouble(), 0.1)
        }
        test(0.0)
        test(PI / 2)
        test(1.0)
        test(0.1)
    }
}
