package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootingState
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.For
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.Repeat
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.geometry.Vector2D
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
            { - driver.leftStick.y.sq },
            {   driver.leftStick.x.sq },

            DoubleSupplier(driver.leftTrigger.sq ::toDouble),
            DoubleSupplier(driver.rightTrigger.sq::toDouble),
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
                ShootingState (
                    { Drivetrain.position.vector },
                    Globals.goalPose,
                    Globals.throughPointOffset
                ),

                Flywheel.stop()
            ).nextCommand())

            a.whileTrue(
                Repeat(3) {(

                    WaitUntilCommand(Robot::readyToShoot)
                    andThen Robot.kickBall()
                    andThen driver.rumble(0.2)

                )}
            )

            b.whileTrue(
                Repeat(3) {
                    Robot.kickBall() andThen driver.rumble(0.2)
                }
            )

            x.onTrue( Intake.reverse() )

        }

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "pinpoint" ids Drivetrain.pinpoint.hardwareDevice::getPosition
            "" ids CommandScheduler::status
        }
    }
}
