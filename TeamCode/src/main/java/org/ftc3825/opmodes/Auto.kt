package org.ftc3825.opmodes

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
import org.ftc3825.subsystem.Localizer
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.util.Pose2D

@Autonomous(name = "Auto", group = "a")
class Auto: CommandOpMode() {
    override fun init() {
        Intake.init(hardwareMap)
        Extendo.init(hardwareMap)
        Drivetrain.init(hardwareMap)
        OuttakeSlides.init(hardwareMap)
        Localizer.init(hardwareMap)

        Localizer.position = Pose2D(6, -72 + 7, 0)

        val startingPath = Path(
            Line(
                6, -72 + 7,
                6, -24 - 7
            )
        )
        val goToSamplePath = Path(
            Spline(
                6, -24-7,
                0, -10,
                24, -40,
                10, 0
            ),
            Spline(
                24, -40,
                20, 0,
                49, -15,
                30, 0
            ),
        )
        val pushLastSamplePath = Path(
            Spline(
                59, -15,
                10, 0,
                72-6, -40,
                0, -20
            ),
            Line(
                72-6, -40,
                72-6, -72 + 7
            )
        )
        val cycleToPath = Path(
            Spline(
                72-6, -72+7,
                0, 20,
                18, -40,
                -30, 10
            ),
            Spline(
                18, -40,
                -12, 4,
                6, -24-7,
                0, 5
            )
        )
        val cycleFromPath = Path(
            Spline(
                6, -24-7,
                0, -5,
                18, -40,
                12, -4
            ),
            Spline(
                18, -40,
                30, -10,
                72-6, -72+7,
                0, -20
            )
        )

        val hangPreload = (
            FollowPathCommand(startingPath)
                andThen OuttakeSlides.extend()
                andThen OuttakeSlides.retract()
        )
        val pushOneSample = (
            DriveCommand(BACK, 24.0)
                parallelTo Extendo.runOnce { it.extend() }
                andThen (
                    DriveCommand(FORWARD, 24.0)
                    parallelTo Extendo.runOnce { it.retract() }
                )
        )
        val pushSamples = (
            FollowPathCommand(goToSamplePath)
                andThen pushOneSample
                andThen DriveCommand(RIGHT, 10.5)
                andThen pushOneSample
        )
        
        val pushLastSample = FollowPathCommand(pushLastSamplePath)

        val cycle = (
            FollowPathCommand(cycleToPath)
                andThen OuttakeSlides.extend()
                andThen OuttakeSlides.retract()
                andThen FollowPathCommand(cycleFromPath)
        )

        (
            hangPreload andThen pushSamples andThen pushLastSample andThen cycle
        ).schedule()

        var i = 0
        RunCommand {
            if(i % 10 == 0){
                println(Localizer.position.vector)
            }
            i ++
        }.schedule()

        initialize()

    }
}
