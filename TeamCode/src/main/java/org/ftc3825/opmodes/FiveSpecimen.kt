package org.ftc3825.opmodes

import org.ftc3825.command.internal.CommandScheduler
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.pedroPathing.pathGeneration.BezierLine
import org.ftc3825.pedroPathing.pathGeneration.Path
import org.ftc3825.pedroPathing.pathGeneration.Point
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.pedroPathing.localization.PinpointLocalizer
import org.ftc3825.pedroPathing.pathGeneration.PathBuilder
import org.ftc3825.pedroPathing.pathGeneration.Point.CARTESIAN
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Globals
import org.ftc3825.util.Pose2D
import kotlin.math.floor
import kotlin.math.PI

@Autonomous(name = "5+0", group = "a")
class FiveSpecimen: CommandOpMode() {
    override fun init() {
        initialize()
        Globals.AUTO = true

        PinpointLocalizer.pinpoint.resetPosAndIMU()
        Drivetrain.positionOffset = Pose2D(
            8, -65, 0.0
        )

        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.grab()
        }.schedule()

        var path1 = Path(
            BezierLine(
                Point(8.0, -65.0, CARTESIAN),
                Point(8.0, -50.0, CARTESIAN)
            )
        )

        var path2 = (
            PathBuilder()
                .addPath(
                    BezierLine(
                        Point(8.0, -50.0, CARTESIAN),
                        Point(62.0, -36.0, CARTESIAN)
                    )
                )
                .addPath(
                    BezierLine(
                        Point(62.0, -36.0, CARTESIAN),
                        Point(62.0, -38.0, CARTESIAN)
                    )
                )
        )
        var path3 = Path(
            Line(
                62,-28,
                8,-50
            ).withHeading( PI / 2 )
        )

        var path4 = Path(
            Line(
                8,-50,
                60,-65
            )
        )

        var placePreload = (
                InstantCommand {
                    Arm.pitchDown()
                    Claw.pitchUp()
                    Claw.grab()
                }
            andThen OuttakeSlides.runToPosition(1300.0)
            andThen FollowPathCommand(path1)
            andThen OuttakeSlides.runToPosition(1450.0)
            andThen InstantCommand { Claw.release() }
        )

        var moveFieldSpecimens = (
                FollowPathCommand(
                    Path(
                        Line(
                            8, -50,
                            35, -50
                        ).withHeading(PI / 2),

                        Line(
                            35, -50,
                            35, -16
                        ).withHeading(PI / 2)
                    )
                )
                andThen  FollowPathCommand (
                    Path(
                        Line(
                            35, -16,
                            45, -16
                        ).withHeading(PI / 2),
                        Line(
                            45, -16,
                            45, -53
                        ).withHeading(PI / 2),

                        Line(
                            45, -53,
                            45, -16
                        ).withHeading(PI / 2)
                    )
                )
                andThen FollowPathCommand(
                    Path(
                        Line(
                            45, -16,
                            55, -16
                        ).withHeading(PI / 2),
                        Line(
                            55, -16,
                            55, -53
                        ).withHeading(PI / 2),

                        Line(
                            55, -53,
                            55, -16
                        ).withHeading(PI / 2)
                    )
                )
                andThen FollowPathCommand(
                    Path(
                        Line(
                            55, -16,
                            62, -16
                        ).withHeading(PI / 2),
                        Line(
                            62, -16,
                            62, -53
                        ).withHeading(PI / 2),
                    )
                )
        )

        var moveToCycle = FollowPathCommand(
            Path(
                Line(
                    66, -55,
                    58, -46
                ),
                Line(
                    58, -46,
                    40, -46
                )

            )
        )
        var cycle = (
                InstantCommand { Claw.pitchUp() }
            parallelTo InstantCommand { Arm.pitchDown() }
            parallelTo OuttakeSlides.runToPosition(200.0)
            andThen InstantCommand { Claw.grab() }
            andThen WaitCommand(0.5)
            andThen OuttakeSlides.runToPosition(1100.0)
            andThen (
                FollowPathCommand(
                    Path(
                        Line(
                            40, -46,
                            8, -50
                        ).withHeading(PI / 2)
                    )
                )
            )
            andThen OuttakeSlides.runToPosition(1000.0)
            andThen InstantCommand { Claw.release() }
            andThen (
                OuttakeSlides.runToPosition(200.0)
                parallelTo FollowPathCommand(
                    Path(
                        Line(
                            8, -50,
                            40, -46
                        ).withHeading( 3 * PI / 2 )

                    )
                )
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
            placePreload
            andThen moveFieldSpecimens
            andThen cycle
            andThen cycle
            andThen cycle
            andThen cycle
        ).schedule()
    }
}
