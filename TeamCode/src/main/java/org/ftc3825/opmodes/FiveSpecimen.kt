package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.FollowPedroPath
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.pedroPathing.localization.PinpointLocalizer
import org.ftc3825.pedroPathing.pathGeneration.BezierCurve
import org.ftc3825.pedroPathing.pathGeneration.BezierLine
import org.ftc3825.pedroPathing.pathGeneration.Path
import org.ftc3825.pedroPathing.pathGeneration.PathBuilder
import org.ftc3825.pedroPathing.pathGeneration.Point
import org.ftc3825.pedroPathing.pathGeneration.Point.CARTESIAN
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Globals
import org.ftc3825.util.Pose2D
import kotlin.math.PI
import kotlin.math.floor


@Autonomous(name = "5+0", group = "a")
class FiveSpecimen: CommandOpMode() {
    override fun init() {
        initialize()
        Globals.AUTO = true

        PinpointLocalizer.pinpoint.resetPosAndIMU()
        Drivetrain.positionOffset = Pose2D(
            8, 66, 0.0
        )

        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.grab()
        }.schedule()

        val path1 = Path(
            BezierLine(
                Point(8.0, 66.0, CARTESIAN),
                Point(23.0, 66.0, CARTESIAN)
            )
        )

        var placePreload = (
                InstantCommand {
                    Arm.pitchDown()
                    Claw.pitchUp()
                    Claw.grab()
                }
            andThen OuttakeSlides.runToPosition(1300.0)
            andThen FollowPedroPath(path1)
            andThen OuttakeSlides.runToPosition(1450.0)
            andThen InstantCommand { Claw.release() }
        )

        var moveFieldSpecimens = FollowPedroPath(
            PathBuilder()
                .addPath(
                    BezierCurve(
                        Point(23.0, 66.0, CARTESIAN),
                        Point(23.0, 20.0, CARTESIAN),
                        Point(21.0, 36.0, CARTESIAN),
                        Point(62.0, 51.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .addPath(
                    BezierLine(
                        Point(63.0, 23.0, CARTESIAN),
                        Point(18.0, 23.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .addPath(
                    BezierLine(
                        Point(18.0, 23.0, CARTESIAN),
                        Point(63.0, 23.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .addPath(
                    BezierLine(
                        Point(63.0, 23.0, CARTESIAN),
                        Point(63.0, 11.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .addPath(
                    BezierLine(
                        Point(63.0, 11.0, CARTESIAN),
                        Point(18.0, 11.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .addPath(
                    BezierLine(
                        Point(18.0, 11.0, CARTESIAN),
                        Point(63.0, 11.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .addPath(
                    BezierLine(
                        Point(63.0, 11.0, CARTESIAN),
                        Point(63.0, 4.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .addPath(
                    BezierLine(
                        Point(63.0, 4.0, CARTESIAN),
                        Point(18.0, 4.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .build()
        )

        var moveToCycle = FollowPedroPath(
            PathBuilder()
                .addPath(
                    BezierCurve(
                        Point(18.0, 4.0, CARTESIAN),
                        Point(29.0, 17.0, CARTESIAN),
                        Point(33.0, 35.0, CARTESIAN),
                        Point(25.0, 35.0, CARTESIAN)
                    )
                ).setConstantHeadingInterpolation(0.0)
                .build()
        )
        var cycle = (
                InstantCommand { Claw.pitchUp() }
            parallelTo InstantCommand { Arm.pitchDown() }
            parallelTo OuttakeSlides.runToPosition(200.0)
            andThen InstantCommand { Claw.grab() }
            andThen WaitCommand(0.5)
            andThen OuttakeSlides.runToPosition(1100.0)
            andThen FollowPedroPath(
                    PathBuilder()
                        .addPath(
                            BezierLine(
                                Point(25.0, 35.0, CARTESIAN),
                                Point(23.0, 66.0, CARTESIAN)
                            )
                        ).setLinearHeadingInterpolation(0.0, PI)
                        .build()
            )
            andThen OuttakeSlides.runToPosition(10.0)
            andThen InstantCommand { Claw.release() }
            andThen (
                OuttakeSlides.runToPosition(200.0)
                parallelTo FollowPedroPath(
                    PathBuilder()
                        .addPath(
                            BezierLine(
                                Point(23.0, 66.0, CARTESIAN),
                                Point(25.0, 35.0, CARTESIAN)
                            )
                        ).setLinearHeadingInterpolation(PI, 0.0)
                        .build()
                )
            )
        )

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("hertz") { floor(1/CommandScheduler.deltaTime) }
        Telemetry.addFunction("") { Drivetrain.position.toString() }
        Telemetry.addFunction("slides") { OuttakeSlides.position }
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
