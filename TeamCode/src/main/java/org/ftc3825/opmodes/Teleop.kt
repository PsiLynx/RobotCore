package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Arm
import org.ftc3825.util.Pose2D
import org.ftc3825.command.internal.Trigger
import org.ftc3825.command.internal.CommandScheduler
import kotlin.math.abs

@TeleOp(name = "FEILD CENTRIC", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()

        OuttakeSlides.reset()
        //OuttakeSlides.motors.forEach { it.encoder?.reset() }
        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.release()
        }.schedule()

        Telemetry.telemetry = telemetry

        var driver = Gamepad(gamepad1!!)
        var operator = Gamepad(gamepad2!!)

        var scale = 1.0;
        Drivetrain.run {
            it.driveFeildCentric(Pose2D(
                  -driver.left_stick_y * scale,
                    driver.left_stick_x * scale,
                    driver.right_stick_x * scale
            ))
        }.schedule()

        driver.right_bumper.onTrue( InstantCommand { scale = 0.25; Unit } )
        driver.right_bumper.onFalse( InstantCommand { scale = 1.0; Unit } )


        driver.left_bumper.onTrue( Claw.toggleGrip() )

        driver.dpad_left.onTrue( Claw.rollLeft() )
        driver.dpad_down.onTrue( Claw.rollCenter() )
        driver.dpad_right.onTrue( Claw.rollRight() )

        Trigger { driver.left_trigger > 0.7 }.onTrue(
            OuttakeSlides.runToPosition(160.0)
                parallelTo Arm.pitchDown()
                parallelTo Claw.pitchDown()
                parallelTo Claw.rollCenter()

        )

        driver.y.onTrue(
            Arm.pitchUp()
                parallelTo Claw.pitchUp()
                parallelTo Claw.rollRight()
        )

        driver.a.onTrue( OuttakeSlides.runToPosition(1600.0) )

        Trigger { driver.right_trigger > 0.7 } .onTrue(
            OuttakeSlides.runToPosition(15.0)
        )


        Telemetry.data = arrayListOf()
        Telemetry.lines = arrayListOf()

        Telemetry.addFunction("power") { driver.left_stick_y }
        Telemetry.addFunction("left") { OuttakeSlides.leftMotor.position }
        Telemetry.addFunction("right") { OuttakeSlides.rightMotor.position }
        Telemetry.addFunction("pos") { Drivetrain.position }
        Telemetry.addFunction("\n") { CommandScheduler.status() }

        
        Telemetry.justUpdate().schedule()
    }
}
