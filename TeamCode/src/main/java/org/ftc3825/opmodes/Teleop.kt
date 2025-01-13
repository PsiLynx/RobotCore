package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.TeleopDrivePowers
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.Trigger
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun init() {
        initialize()

        OuttakeSlides.reset()
        Arm.reset()
        Claw.reset()
        Drivetrain.reset()
        Telemetry.reset()

        Drivetrain.position = Pose2D(0.0, 0.0, 0.0)

        Claw.justUpdate().schedule()

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)

        var slowMode = false
        fun transMul() = if(slowMode) 0.25 else 1.0
        fun rotMul() = if(slowMode) 0.25 else 0.5

        TeleopDrivePowers(
            { -driver.leftStickYSq * transMul() },
            { driver.leftStickXSq * transMul() },
            { -driver.rightStickXSq * rotMul() }
        ).schedule()

        driver.rightBumper.onTrue( InstantCommand { slowMode = true } )
        driver.rightBumper.onFalse( InstantCommand { slowMode = false } )

        driver.leftBumper.onTrue( Claw.toggleGrip() )

        driver.x.onTrue( InstantCommand { Drivetrain.position.heading = Rotation2D(0) } )
        driver.y.onTrue( Arm.pitchUp() parallelTo Claw.rollRight() )
        driver.a.onTrue( Claw.outtakePitch() parallelTo OuttakeSlides.extend() )

        Trigger { driver.rightTrigger > 0.7 }.onTrue( Claw.pitchDown() parallelTo OuttakeSlides.retract() )
        Trigger { driver.leftTrigger  > 0.7 }.onTrue(
            Command.parallel(
                OuttakeSlides.runToPosition(340.0),
                Arm.pitchDown(),
                Claw.pitchDown()
            )
        )

        driver.dpadLeft.onTrue( Claw.rollLeft() )
        driver.dpadDown.onTrue( Claw.rollCenter() )
        driver.dpadRight.onTrue( Claw.rollRight() )

        Telemetry.addAll {
           "left trigger"  ids { driver.leftTrigger }
            "slides"       ids { OuttakeSlides.leftMotor.position }
            "claw"         ids { Claw.pitch }
            "position"     ids { (Drivetrain.position) }
            newLine()
            ""             ids { CommandScheduler.status() }
        }
    }
}