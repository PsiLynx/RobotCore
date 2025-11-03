package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootingState
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.Repeat
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Transfer
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.log
import java.util.function.DoubleSupplier
import kotlin.math.PI

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun initialize() {

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
            { driver.leftTrigger.isTriggered }
        )
        dtControl.schedule()

        Transfer.stop().schedule()

        driver.apply {
            leftBumper.onTrue(CyclicalCommand(

                Intake.run()
                parallelTo (
                    WaitUntilCommand { Transfer.pressed }
                    andThen Transfer.runToPos(0.6)
                ),

                Intake.stop()

            ).nextCommand())


            rightBumper.onTrue(CyclicalCommand(
                Flywheel.shootingState {
                    ( Globals.goalPose.groundPlane -
                    Drivetrain.position.vector
                    ).mag
               },

                Flywheel.stop()
            ).nextCommand())

            a.whileTrue(
                Flywheel.setPower(-0.8)
                parallelTo Transfer.run {
                    it.servo.position = 0.3
                }
            )

            rightTrigger.whileTrue(
                (Transfer.run() parallelTo Intake.run())
            ).onFalse(Transfer.stop())

            x.whileTrue( Intake.reverse() )

        }
        RunCommand {
            log("alliance") value Globals.alliance.toString()
        }
        operator.apply {
            dpadUp.onTrue(InstantCommand { FlywheelConfig.MAX_VEL -= 10})
            dpadDown.onTrue(InstantCommand { FlywheelConfig.MAX_VEL += 10})
        }

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "pinpoint" ids Drivetrain.pinpoint.hardwareDevice::getPosition
            "" ids CommandScheduler::status
        }
    }
}
