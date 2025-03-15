package org.firstinspires.ftc.teamcode.opmodes

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry

//@TeleOp(name = "Test Everything")
class TestEverything: CommandOpMode() {
    override fun initialize() {
        Drivetrain.reset()
//        Extendo.reset()
//        SampleIntake.reset()
//        OuttakeArm.reset()
//        OuttakeClaw.reset()
        Telemetry.reset()

        val driver = Gamepad(gamepad1!!)

        Drivetrain.run {
            it.setWeightedDrivePower(
                driver.leftStick.x.sq,
                driver.leftStick.y.sq,
                driver.rightStick.x.sq,
            )
        }.schedule()
//        driver.dpadUp   .whileTrue( Extendo.setPowerCommand( 0.0,  0.5) )
//        driver.dpadDown .whileTrue( Extendo.setPowerCommand( 0.0, -0.5) )
//        driver.dpadLeft .whileTrue( Extendo.setPowerCommand(-0.5,  0.0) )
//        driver.dpadRight.whileTrue( Extendo.setPowerCommand( 0.5,  0.0) )
//
//        driver.leftBumper.whileTrue( OuttakeArm.setPower(-0.5) )
//        Trigger { driver.leftTrigger > 0.7 }.whileTrue( OuttakeArm.setPower(0.5) )
//
//        driver.rightBumper.onTrue( OuttakeClaw.toggleGrip() )
//        driver.a.toggleOnTrue(
//            StartEndCommand(
//                OuttakeClaw.pitchUp(),
//                OuttakeClaw.pitchDown()
//            )
//        )
//        driver.b.toggleOnTrue(
//            StartEndCommand(
//                OuttakeClaw.rollCenter(),
//                OuttakeClaw.rollRight()
//            )
//        )
//        driver.x.toggleOnTrue(
//            StartEndCommand(
//                SampleIntake.pitchBack(),
//                SampleIntake.pitchDown()
//            )
//        )
//        driver.y.toggleOnTrue(
//            StartEndCommand(
//                SampleIntake.rollCenter(),
//                SampleIntake.rollRight()
//            )
//        )
//
        Telemetry.addAll {
            "left stick x" ids { driver.leftStick.x.sq }
            "" ids CommandScheduler::status
        }
        Telemetry.justUpdate().schedule()
//            "pos" ids { Drivetrain.position }
//            newLine()
//            "slide position (x, y)" ids { Extendo.position }
//            "slide switch"          ids Extendo.xTouchSensor::status
//            "gantry switch"         ids Extendo.xTouchSensor::status
//            newLine()
//            "arm pitch"      ids { OuttakeArm.angle.toString() + " rad" }
//            "arm ticks"      ids { OuttakeArm.position }
//            "outtake switch" ids OuttakeArm.touchSensor::status
//            newLine()
//            "samples" ids {
//                "[\n" + Extendo.samples.joinToString { "\t$it\n" } + "]"
//            }
//
//        }
    }
}