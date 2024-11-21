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
import org.ftc3825.component.Motor

@TeleOp(name = "FEILD CENTRIC", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()

        OuttakeSlides.reset()
        Arm.reset()
        Claw.reset()
        Drivetrain.reset()
        //OuttakeSlides.components.forEach { it.encoder?.resetPosition() }
        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.release()
        }.schedule()

        Telemetry.telemetry = telemetry

        var driver = Gamepad(gamepad1!!)
        var operator = Gamepad(gamepad2!!)

        var scale = 1.0
        Drivetrain.run {
            it.driveFieldCentric(Pose2D(
                  -driver.left_stick_y_sq * scale,
                    driver.left_stick_x_sq * scale,
                    -driver.right_stick_x_sq * scale * 0.5
            ))
        }.schedule()

        driver.right_bumper.onTrue( InstantCommand { scale = 0.25; } )
        driver.right_bumper.onFalse( InstantCommand { scale = 1.0; } )

        driver.b.onTrue(
            InstantCommand{
                OuttakeSlides.components.forEach { if(it is Motor) it.encoder!!.resetPosition() }
//                Arm.pitchDown()
//                Claw.pitchDown()
            }
        )

        driver.x.onTrue(
            InstantCommand {
                //Drivetrain.follower.poseUpdater.pose.heading = 0.0
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
            } parallelTo OuttakeSlides.runToPosition(350.0)
        )

        driver.y.onTrue(
            InstantCommand {
                Arm.pitchUp()
                Claw.pitchUp()
                Claw.rollRight()
            }
        )

        driver.a.onTrue( OuttakeSlides.extend() )

        Trigger { driver.right_trigger > 0.7 } .onTrue(
            OuttakeSlides.retract()
        )

        OuttakeSlides.justUpdate().schedule()


        Telemetry.data = arrayListOf()
        Telemetry.lines = arrayListOf()

        Telemetry.addFunction("power") { OuttakeSlides.leftMotor.lastWrite ?: 0.0 }
        Telemetry.addFunction("left") { OuttakeSlides.leftMotor.position }
        Telemetry.addFunction("right") { OuttakeSlides.rightMotor.position }
        Telemetry.addFunction("pos") { Drivetrain.position }
        Telemetry.addFunction("left trigger") { driver.left_trigger }
        Telemetry.addFunction("\n") { CommandScheduler.status() }

        
        Telemetry.justUpdate().schedule()
    }
}
