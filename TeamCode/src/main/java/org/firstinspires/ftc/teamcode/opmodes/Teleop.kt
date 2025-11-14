package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.AltShootingState
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.subsystem.Cameras
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.log

@TeleOp(name = " ROBOT CENTRIC")
class Teleop: CommandOpMode() {
    override fun preSelector() {
        Cameras.justUpdate().schedule()
    }
    override fun postSelector() {

        // Set position
        //Drivetrain.position = Pose2D(-72 + 7.75 + 8, 72 - 22.5 - 7, -PI/2)

        Drivetrain.ensurePinpointSetup()
        InstantCommand {
            println("all hubs: ")
            println(this.allHubs.joinToString())
            println("^^^")
        }.schedule()

        // Drivetrain.justUpdate().schedule()


        val dtControl = TeleopDrivePowers(
            { - driver.leftStick.y.sq },
            {   driver.leftStick.x.sq },

            { - driver.rightStick.x.sq },
            driver.a.supplier
        )
        dtControl.schedule()

        driver.apply {
            leftBumper.onTrue(Intake.run())
            leftTrigger.onTrue(Intake.stop())


            rightBumper.onTrue(CyclicalCommand(
                Flywheel.stop(),

                AltShootingState(Drivetrain::position)
            ).nextCommand())

            rightTrigger.whileTrue(
                Robot.kickBalls()
            )

            x.whileTrue( Intake.reverse() )

        }
        RunCommand {
            log("alliance") value Globals.alliance.toString()
        }

        RunCommand {
            componentHubs.forEach { hub ->
                if(Robot.readyToShoot){
                    hub.ledColor = 0x00FF00
                }
                else if(Drivetrain.tagReadGood){
                    hub.ledColor = 0xFF00FF
                }
                else {
                    hub.ledColor = 0xFF0000
                }
            }
        }.schedule()

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "pinpoint" ids Drivetrain.pinpoint.hardwareDevice::getPosition
            "" ids CommandScheduler::status
        }
    }
}
