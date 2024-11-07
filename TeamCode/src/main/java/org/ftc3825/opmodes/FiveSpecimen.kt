package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.FollowPedroPath
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
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
    fun PathBuilder.pushOneSample(startY: Number, endY: Number) = this.addPath(
        BezierCurve(
            Point(25.0, startY.toDouble(), CARTESIAN),
            Point(60.0, startY.toDouble(), CARTESIAN),
            Point(60.0, (endY.toDouble() + startY.toDouble()) / 2, CARTESIAN)
        )
    ).setConstantHeadingInterpolation(0.0)
        .addPath(
            BezierCurve(
                Point(60.0, (endY.toDouble() + startY.toDouble()) / 2, CARTESIAN),
                Point(60.0, endY.toDouble(), CARTESIAN),
                Point(25.0, endY.toDouble(), CARTESIAN)
            )
        ).setConstantHeadingInterpolation(0.0)
//            BezierCurve(
//                Point(30.0, startY.toDouble(), CARTESIAN),
//                Point(43.0, startY.toDouble(), CARTESIAN),
//                Point(63.0, (endY.toDouble() + startY.toDouble()) / 2 + 5, CARTESIAN),
//                Point(63.0, (endY.toDouble() + startY.toDouble()) / 2, CARTESIAN)
//            )
//        ).setConstantHeadingInterpolation(0.0)
//        .addPath(
//            BezierCurve(
//                Point(63.0, (endY.toDouble() + startY.toDouble()) / 2, CARTESIAN),
//                Point(63.0, (endY.toDouble() + startY.toDouble()) / 2 - 5, CARTESIAN),
//                Point(43.0, endY.toDouble(), CARTESIAN),
//                Point(30.0, endY.toDouble(), CARTESIAN),
//                )
//        ).setConstantHeadingInterpolation(0.0)
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

        val path1 = PathBuilder()
            .addPath(
                BezierLine(
                    Point(8.0, 66.0, CARTESIAN),
                    Point(23.0, 66.0, CARTESIAN)
                )
            )
            .build()



        var placePreload = (
//            OuttakeSlides.runToPosition(1470.0)
//            andThen InstantCommand {
//                Arm.pitchDown()
//                Claw.pitchUp()
//                Claw.grab()
//                Claw.rollCenter()
//            }
             FollowPedroPath(path1)
//            andThen OuttakeSlides.runToPosition(1620.0)
//            andThen InstantCommand {
//                Claw.release()
//                Arm.pitchUp()
//            }

        )

        var moveFieldSpecimens = ( /*OuttakeSlides.retract() andThen */ InstantCommand { } ) parallelTo (
            FollowPedroPath(
                PathBuilder()
//                    .addPath(
//                        BezierCurve(
//                            Point(23.0, 66.0, CARTESIAN),
//                            Point(23.0, 20.0, CARTESIAN),
//                            Point(62.0, 45.0, CARTESIAN),
//                            Point(62.0, 27.0, CARTESIAN)
//                        )
//                    ).setConstantHeadingInterpolation(0.0)
//                    .addPath(
//                        BezierCurve(
//                            Point(63.0, 20.0, CARTESIAN),
//                            Point(63.0, 17.0, CARTESIAN),
//                            Point(43.0, 40.0, CARTESIAN),
//                            Point(30.0, 40.0, CARTESIAN),
//                        )
//                    ).setConstantHeadingInterpolation(0.0)
                    .addPath(
                        BezierCurve(
                            Point(23.0, 66.0, CARTESIAN),
                            Point(23.0, 37.0, CARTESIAN),
                            Point(54.0, 37.0, CARTESIAN)
                        )
                    ).setConstantHeadingInterpolation(0.0)
                    .addPath(
                        BezierCurve(
                            Point(54.0, 37.0, CARTESIAN),
                            Point(60.0, 37.0, CARTESIAN),
                            Point(60.0, 25.0, CARTESIAN)
                        )
                    ).setConstantHeadingInterpolation(0.0)
                    .setZeroPowerAccelerationMultiplier(10.0)
                    .build()
            ) andThen FollowPedroPath(
                PathBuilder()
                    .addPath(
                        BezierLine(
                            Point(60.0, 25.0, CARTESIAN),
                            Point(25.0, 25.0, CARTESIAN)
                        )
                    ).setConstantHeadingInterpolation(0.0)


                    .pushOneSample(25, 15)
                    .pushOneSample(15, 10)
                    .addPath(
                        BezierLine(
                            Point(25.0, 10.0, CARTESIAN),
                            Point(20.0, 10.0, CARTESIAN)
                        )
                    ).setConstantHeadingInterpolation(0.0)
                    .build()
            )
        ) andThen InstantCommand { Drivetrain.follower.setMaxPower(1.0) }

        var moveToCycle = FollowPedroPath(
            PathBuilder()
                .addPath(
                    BezierCurve(
                        Point(20.0, 10.0, CARTESIAN),
                        Point(29.0, 17.0, CARTESIAN),
                        Point(33.0, 35.0, CARTESIAN),
                        Point(25.0, 35.0, CARTESIAN)
                    )
                ).setLinearHeadingInterpolation(0.0, PI)
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
                                Point(23.0, 60.0, CARTESIAN)
                            )
                        ).setLinearHeadingInterpolation(PI, 0.0)
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
                                Point(23.0, 60.0, CARTESIAN),
                                Point(25.0, 35.0, CARTESIAN)
                            )
                        ).setLinearHeadingInterpolation(0.0, PI)
                        .build()
                )
            )
        )
    var lastTime = System.nanoTime()
    var currentTime = System.nanoTime()

    RunCommand {
        lastTime = currentTime
        currentTime = System.nanoTime()
        Unit
    }.schedule()

        (
            placePreload
            andThen moveFieldSpecimens
            andThen moveToCycle
            andThen cycle
            andThen cycle
            andThen cycle
            andThen cycle
        ).schedule()

    Telemetry.telemetry = telemetry!!
    Telemetry.addFunction("hertz") { floor(1 / ((currentTime - lastTime) * 1e-9)) }
    Telemetry.addFunction("") { Drivetrain.follower.pose }
    Telemetry.addFunction("slides") { OuttakeSlides.position }
    Telemetry.addFunction("\n") { CommandScheduler.status() }
    Telemetry.justUpdate().schedule()
}
}
