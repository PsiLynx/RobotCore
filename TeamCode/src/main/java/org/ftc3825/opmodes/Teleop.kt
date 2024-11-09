package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Arm
import org.ftc3825.util.Pose2D
import org.ftc3825.command.internal.Trigger
import org.ftc3825.command.internal.CommandScheduler

@TeleOp(name = "FEILD CENTRIC", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()

        OuttakeSlides.reset()
        Drivetrain.follower.breakFollowing()
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
            it.driveFieldCentric(Pose2D(
                  -driver.left_stick_y * scale,
                    driver.left_stick_x * scale,
                    -driver.right_stick_x * scale
            ))
        }.schedule()

        driver.right_bumper.onTrue( InstantCommand { scale = 0.25; Unit } )
        driver.right_bumper.onFalse( InstantCommand { scale = 1.0; Unit } )

        driver.b.onTrue(
            InstantCommand{
                OuttakeSlides.motors.forEach { it.encoder!!.reset() }
//                Arm.pitchDown()
//                Claw.pitchDown()
            }
        )

        driver.x.onTrue(
            Drivetrain.run {
                it.position.heading = 0.0
                Unit
            }
        )


        driver.left_bumper.onTrue( InstantCommand { Claw.toggleGrip() } )

        driver.dpad_left.onTrue( InstantCommand { Claw.rollLeft() } )
        driver.dpad_down.onTrue( InstantCommand { Claw.rollCenter() } )
        driver.dpad_right.onTrue( InstantCommand { Claw.rollRight() } )

        Trigger { driver.left_trigger > 0.7 }.onTrue(
            InstantCommand {
                Arm.pitchDown()
                Claw.pitchDown()
                Claw.rollCenter()
            } andThen OuttakeSlides.runToPosition(350.0)
        )

        driver.y.onTrue(
            InstantCommand {
                Arm.pitchUp()
                Claw.pitchUp()
                Claw.rollRight()
            }
        )

        driver.a.onTrue( OuttakeSlides.runToPosition(1600.0) )

        Trigger { driver.right_trigger > 0.7 } .onTrue(
            OuttakeSlides.runToPosition(15.0)
        )


        Telemetry.data = arrayListOf()
        Telemetry.lines = arrayListOf()

        Telemetry.addFunction("power") { OuttakeSlides.leftMotor.lastWrite ?: 0.0 }
        Telemetry.addFunction("left") { OuttakeSlides.leftMotor.position }
        Telemetry.addFunction("right") { OuttakeSlides.rightMotor.position }
        Telemetry.addFunction("pos") { Drivetrain.position }
        Telemetry.addFunction("\n") { CommandScheduler.status() }

        
        Telemetry.justUpdate().schedule()
    }
}
