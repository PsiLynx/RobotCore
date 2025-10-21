package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.RobotLog.a
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.Trigger
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.Telemetry.ids
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import java.util.function.DoubleSupplier
import kotlin.math.PI

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun initialize() {

        // Set position
        Drivetrain.position = Pose2D(-72 + 7.75 + 8, 72 - 22.5 - 7, -PI/2)

        // Temp
        Globals.alliance = Globals.Alliance.BLUE

        var slowMode = false
        fun transMul() = if(slowMode) 0.25 else 1.0
        fun rotMul() = if(slowMode) 0.5 else 1.0

        Drivetrain.ensurePinpointSetup()
        InstantCommand {
            println("all hubs: ")
            println(this.allHubs.joinToString())
            println("^^^")
        }.schedule()

        // Drivetrain.justUpdate().schedule()


        val dtControl = TeleopDrivePowers(
            { - driver.leftStick.y.sq  * transMul() },
            {   driver.leftStick.x.sq  * transMul() },

            DoubleSupplier(driver.leftTrigger ::toDouble),
            DoubleSupplier(driver.rightTrigger::toDouble),
        )
        dtControl.schedule()

        Kicker.open().schedule()

        driver.apply {
            leftBumper.onTrue(CyclicalCommand(

                Intake.run()
                parallelTo (
                    WaitUntilCommand { Kicker.pressed }
                    andThen Kicker.runToPos(0.6)
                ),

                Intake.stop()

            ).nextCommand())


            rightBumper.onTrue(CyclicalCommand(
                Flywheel.shootingState {
                    (Drivetrain.position - Globals.goalPose).mag
                },

                Flywheel.stop()
            ).nextCommand())

            a.whileTrue(
                WaitUntilCommand {
                    Flywheel.readyToShoot && Drivetrain.readyToShoot
                }
                andThen Kicker.close()
                andThen WaitCommand(1)
                andThen Kicker.open()
                andThen WaitCommand(1)
                andThen ( Intake.run() withTimeout 4 )
            )

            b.onTrue(
                Kicker.close()
                andThen WaitCommand(1.3)
                andThen Kicker.open()
                andThen WaitCommand(1)
                andThen (
                    Intake.run()
                    until { Kicker.pressed }
                    withTimeout 3
                )
            )

        }

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "pinpoint" ids Drivetrain.pinpoint.hardwareDevice::getPosition
            "" ids CommandScheduler::status
        }
    }
}
