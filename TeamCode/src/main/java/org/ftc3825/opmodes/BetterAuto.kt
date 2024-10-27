package org.ftc3825.opmodes

import org.ftc3825.command.internal.CommandScheduler
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Path
import org.ftc3825.GVF.Spline
import org.ftc3825.command.DriveCommand
import org.ftc3825.command.DriveCommand.Direction.FORWARD
import org.ftc3825.command.DriveCommand.Direction.BACK
import org.ftc3825.command.DriveCommand.Direction.RIGHT
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Intake
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.util.Pose2D
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.command.internal.TimedCommand
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Slides
import kotlin.math.floor
import kotlin.math.abs
import kotlin.math.PI

@Autonomous(name = "1+1", group = "a")
class BetterAuto: CommandOpMode() {
    override fun init() {
        initialize()

        Drivetrain.imu.resetYaw()
        Drivetrain.encoders.forEach { it.reset() }
        Drivetrain.position = Pose2D(8, -65, 0)

        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.grab()
        }.schedule()

        var path1 = Path(
            Line(
                8,-65,
                8,-50
            )
        )
        var path2 = Path(
            Line(
                8,-50,
                62,-38
            ).withHeading( 3 * PI / 2 ),
            
        )
        var path3 = Path(
            Line(
                62,-40,
                62,-45
            )
        )

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("hertz") { floor(1/CommandScheduler.deltaTime) }
        Telemetry.addFunction("") { Drivetrain.position.toString() }
        Telemetry.addFunction("vector") { path1.pose(Drivetrain.position)}
        Telemetry.addFunction("slides") { OuttakeSlides.position }
        Telemetry.addFunction("endHeading") { path3[-1].endHeading }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()

        (
            InstantCommand {
                Arm.pitchDown() 
                Claw.pitchUp()
                Claw.grab()
            }
                andThen OuttakeSlides.runToPosition(1400.0)
                andThen FollowPathCommand(path1)
                andThen InstantCommand { Claw.release() }
                andThen OuttakeSlides.runToPosition(200.0)
                andThen FollowPathCommand(path2)
                /*andThen (
                    RunCommand {
                        Drivetrain.setWeightedDrivePower(
                            0.0,
                            0.0,
                            (
                                PI - Drivetrain.position.heading 
                            ).coerceIn(-0.2, 0.2)
                        )
                    }
                    until { abs( PI - Drivetrain.position.heading ) < 0.1 }
                )*/
                andThen TimedCommand(0.5) { 
                    Drivetrain.setWeightedDrivePower(0.0, -0.2, 0.0)
                } 
                /*andThen FollowPathCommand(path3)
                andThen InstantCommand { Claw.grab() }
                andThen OuttakeSlides.runToPosition(1400.0)
                */
        ).schedule()
    }
}
