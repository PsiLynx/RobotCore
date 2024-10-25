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

@Autonomous(name = "1+1", group = "a")
class BetterAuto: CommandOpMode() {
    override fun init() {
        initialize()
        Drivetrain.position = Pose2D(6, -65, 0.0)
        Drivetrain.imu.resetYaw()

        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.grab()
        }.schedule()

        var path1 = Path(
            Line(
                6,-65,
                6,-54
            )
        )
        var path2 = Path(
            Line(
                6,-49,
                64,-45
            ),
            Line(
                64,-45,
                6,-49
            ),
//            Line(
//                64,-49,
//                6,-65
//            ),
        )

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("") { Drivetrain.position.toString() }
        Telemetry.addFunction("vector") { path1.pose(Drivetrain.position)}
        Telemetry.addFunction("slides") { OuttakeSlides.position }
        Telemetry.justUpdate().schedule()

        (
                InstantCommand { Arm.pitchDown() }
                        andThen (OuttakeSlides.runToPosition(1400.0))
                        andThen FollowPathCommand(path1)
                ).schedule()
    }
}
