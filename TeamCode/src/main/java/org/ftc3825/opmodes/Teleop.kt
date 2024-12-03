package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
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
import org.ftc3825.util.Rotation2D

@TeleOp(name = "FIELD CENTRIC", group = "a")
class Teleop: CommandOpMode() {
    override fun init() {
        initialize()

        OuttakeSlides.reset()
        Arm.reset()
        Claw.reset()
        Drivetrain.reset()
        Telemetry.reset()

        Telemetry.telemetry = telemetry!!
        Telemetry.justUpdate().schedule()

        ( Arm.pitchUp() parallelTo Claw.grab() ).schedule()
        Claw.pitchDown().schedule()

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)

        var dtSpeed = 1.0
        Drivetrain.run {
            it.setTeleopPowers(
                  -driver.leftStickYSq * dtSpeed,
                  driver.leftStickXSq * dtSpeed,
                  -driver.rightStickXSq * dtSpeed * 0.5
            )
        }.schedule()
        driver.rightBumper.onTrue( InstantCommand { dtSpeed = 0.25; } )
        driver.rightBumper.onFalse( InstantCommand { dtSpeed = 1.0; } )

        driver.leftBumper.onTrue( Claw.toggleGrip() )

        driver.x.onTrue( InstantCommand { Drivetrain.position.heading = Rotation2D(0) } )
        driver.y.onTrue( Arm.pitchUp() parallelTo Claw.rollRight() )
        driver.a.onTrue( OuttakeSlides.extend() )

        Trigger { driver.rightTrigger > 0.7 }.onTrue( OuttakeSlides.retract() )
        Trigger { driver.leftTrigger  > 0.7 }.onTrue(
            Command.parallel(
                Arm.pitchDown(),
                Claw.rollCenter(),
                OuttakeSlides.runToPosition(350.0)
            )
        )

        driver.dpadLeft.onTrue( Claw.rollLeft() )
        driver.dpadDown.onTrue( Claw.rollCenter() )
        driver.dpadRight.onTrue( Claw.rollRight() )
//        driver.dpadUp.onTrue( Claw.pitchUp() )
//        driver.dpadDown.onTrue( Claw.pitchDown() )

        Telemetry.addAll {
            "claw"         to { Claw.pitch }
            "error"        to { Claw.pitchServo.error }
            "feedback"     to { Claw.pitchServo.feedback }
            "target"       to { Claw.pitchServo.setpoint }
            "use feedback" to { Claw.pitchServo.useFeedback }
            "power"        to { Claw.pitchServo.lastWrite }
            "slides"       to { OuttakeSlides.leftMotor.position }
            "\n".add()
            "" to { CommandScheduler.status() }
        }
    }
}