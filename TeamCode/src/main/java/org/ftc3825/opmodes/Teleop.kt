package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
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

@TeleOp(name = "FEILD CENTRIC", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()

        OuttakeSlides.reset()
        Arm.reset()
        Claw.reset()
        Drivetrain.reset()
        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.release()
        }.schedule()

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)

        var scale = 1.0
        Drivetrain.run {
            it.driveFieldCentric(Pose2D(
                  -driver.leftStickYSq * scale,
                    driver.leftStickXSq * scale,
                    -driver.rightStickXSq * scale * 0.5
            ))
        }.schedule()

        driver.rightBumper.onTrue( InstantCommand { scale = 0.25; } )
        driver.rightBumper.onFalse( InstantCommand { scale = 1.0; } )

        driver.b.onTrue(
            InstantCommand{
                OuttakeSlides.motors.forEach{ it.encoder!!.resetPosition() }
//                Arm.pitchDown()
//                Claw.pitchDown()
            }
        )

        driver.x.onTrue(
            InstantCommand {
                //Drivetrain.follower.poseUpdater.pose.heading = 0.0
            }
        )


        driver.leftBumper.onTrue( InstantCommand { Claw.toggleGrip() } )

        driver.dpadLeft.onTrue( InstantCommand { Claw.rollLeft() } )
        driver.dpadDown.onTrue( InstantCommand { Claw.rollCenter() } )
        driver.dpadRight.onTrue( InstantCommand { Claw.rollRight() } )

        Trigger { driver.leftTrigger > 0.7 }.onTrue(
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

        Trigger { driver.rightTrigger > 0.7 } .onTrue(
            OuttakeSlides.retract()
        )

        OuttakeSlides.justUpdate().schedule()


        Telemetry.telemetry = telemetry!!
        Telemetry.data = arrayListOf()
        Telemetry.lines = arrayListOf()

        Telemetry.addFunction("power") { OuttakeSlides.leftMotor.lastWrite }
        Telemetry.addFunction("left") { OuttakeSlides.leftMotor.position }
        Telemetry.addFunction("right") { OuttakeSlides.rightMotor.position }
        Telemetry.addFunction("pos") { Drivetrain.pos }
        Telemetry.addFunction("left trigger") { driver.leftTrigger }
        Telemetry.addFunction("\n") { CommandScheduler.status() }

        
        Telemetry.justUpdate().schedule()
    }
}
