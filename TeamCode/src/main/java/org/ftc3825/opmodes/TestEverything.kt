package org.ftc3825.opmodes

import org.ftc3825.command.TeleopDrivePowers
import org.ftc3825.command.internal.StartEndCommand
import org.ftc3825.command.internal.Trigger
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
import org.ftc3825.subsystem.Telemetry

class TestEverything: CommandOpMode() {
    override fun init() {
        initialize()
        Drivetrain.reset()
        Extendo.reset()
        SampleIntake.reset()
        OuttakeArm.reset()
        OuttakeClaw.reset()
        Telemetry.reset()
        Telemetry.telemetry = telemetry

        val driver = Gamepad(gamepad1!!)

        TeleopDrivePowers(
            { driver.leftStickYSq.toDouble()  },
            { driver.leftStickXSq.toDouble()  },
            { driver.rightStickXSq.toDouble() },
        )
        driver.dpadUp   .whileTrue( Extendo.setPowerCommand( 0.0,  0.5) )
        driver.dpadDown .whileTrue( Extendo.setPowerCommand( 0.0, -0.5) )
        driver.dpadLeft .whileTrue( Extendo.setPowerCommand(-0.5,  0.0) )
        driver.dpadRight.whileTrue( Extendo.setPowerCommand( 0.5,  0.0) )

        driver.leftBumper.whileTrue( OuttakeArm.setPower(-0.5) )
        Trigger { driver.leftTrigger > 0.7 }.whileTrue( OuttakeArm.setPower(0.5) )

        driver.rightBumper.onTrue( OuttakeClaw.toggleGrip() )
        driver.a.toggleOnTrue(
            StartEndCommand(
                OuttakeClaw.pitchUp(),
                OuttakeClaw.pitchDown()
            )
        )
        driver.b.toggleOnTrue(
            StartEndCommand(
                OuttakeClaw.rollCenter(),
                OuttakeClaw.rollRight()
            )
        )
        driver.x.toggleOnTrue(
            StartEndCommand(
                SampleIntake.pitchBack(),
                SampleIntake.pitchDown()
            )
        )
        driver.y.toggleOnTrue(
            StartEndCommand(
                SampleIntake.rollCenter(),
                SampleIntake.rollRight()
            )
        )

        Telemetry.addAll {
            "pos" ids { Drivetrain.position }
            newLine()
            "slide ticks"    ids { Extendo.leftMotor.position }
            "slide switch"   ids Extendo.xTouchSensor::status
            "gantry ticks"   ids { Extendo.xAxisServo.position }
            "gantry switch"  ids Extendo.xTouchSensor::status
            newLine()
            "arm pitch"      ids { OuttakeArm.angle.toString() + " rad" }
            "arm ticks"      ids { OuttakeArm.position }
            "outtake switch" ids OuttakeArm.touchSensor::status
            newLine()
            "samples"        ids {
                "[\n" + Extendo.samples.joinToString { "\t$it\n" } + "]"
            }

        }
    }
}