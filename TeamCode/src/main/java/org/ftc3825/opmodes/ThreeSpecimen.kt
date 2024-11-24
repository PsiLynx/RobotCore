package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.FollowPedroPath
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.pedroPathing.pathGeneration.BezierLine
import org.ftc3825.pedroPathing.pathGeneration.PathBuilder
import org.ftc3825.pedroPathing.pathGeneration.Point
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Globals
import org.ftc3825.util.Pose2D
import kotlin.math.PI
import kotlin.math.floor


@Autonomous(name = "3+0", group = "a")
class ThreeSpecimen: CommandOpMode() {
    override fun init() {
        initialize()
        Globals.AUTO = true
        Drivetrain.pos = Pose2D(8.0, 66.0, 0.0)
        Command.parallel(
            Arm.pitchUp(),
            Claw.pitchUp(),
            Claw.grab()
        ).schedule()

        val path1 = PathBuilder()
            .addPath(
                BezierLine(
                    Point(8.0, 66.0),
                    Point(36.0, 66.0)
                )
            ).setConstantHeadingInterpolation(0.0)
            .build()

        val placePreload = (
            Command.parallel(
                Arm.pitchUp(),
                Claw.pitchUp(),
                Claw.rollCenter(),
                Claw.grab()
            )
            andThen (
                OuttakeSlides.runToPosition(440.0)
                parallelTo ( FollowPedroPath(path1) )
            )
            andThen (
                OuttakeSlides.run { it.setPower(0.5) }
                    withEnd { OuttakeSlides.setPower(0.0) }
                    withTimeout(0.5)
            )
            andThen Claw.release()
        )

//        var moveFieldSpecimens = (
//            FollowPedroPath(
//                PathBuilder()
//                    .addPath(
//                        BezierCurve(
//                            Point(23.0, 66.0),
//                            Point(23.0, 37.0),
//                            Point(54.0, 37.0)
//                        )
//                    ).setConstantHeadingInterpolation(0.0)
//                    .addPath(
//                        BezierCurve(
//                            Point(54.0, 37.0),
//                            Point(60.0, 37.0),
//                            Point(60.0, 26.0)
//                        )
//                    ).setConstantHeadingInterpolation(0.0)
//                    .build()
//            ) andThen FollowPedroPath(
//                PathBuilder()
//                    .addPath(
//                        BezierLine(
//                            Point(60.0, 26.0),
//                            Point(23.0, 26.0)
//                        )
//                    ).setConstantHeadingInterpolation(0.0)
//                    .build()
//            )
//        ) andThen InstantCommand { Drivetrain.setMaxFollowerPower(1.0) }

        val cycleHumanPlayer = (
            OuttakeSlides.runToPosition(200.0)
            parallelTo (
                FollowPedroPath(
                    PathBuilder()
                        .addPath(
                            BezierLine(
                                Point(36.0, 66.0),
                                Point(13.1, 60.0)
                            )
                        ).setLinearHeadingInterpolation(0.0, - PI / 2)
                        .build()
                )
            )
            andThen Command.parallel(
                Arm.pitchDown(),
                Claw.groundSpecimenPitch(),
                Claw.release(),
                FollowPedroPath(
                    PathBuilder()
                        .addPath(
                            BezierLine(
                                Point(13.1, 60.0),
                                Point(13.1, 48.0),
                            )
                        ).setConstantHeadingInterpolation(- PI / 2)
                        .setPathEndTranslationalConstraint(0.05)
                        .build()
                )
            )
            andThen ( OuttakeSlides.retract() withTimeout(0.5) )
            andThen ( Claw.grab() parallelTo WaitCommand(0.5) )
            andThen ( Arm.pitchUp() parallelTo Claw.pitchUp() )
        )

        val cycleBar = (
            OuttakeSlides.runToPosition(400.0)
            parallelTo (
                FollowPedroPath(
                    PathBuilder()
                        .addPath(
                            BezierLine(
                                Point(13.2, 48.0),
                                Point(13.2, 60.0),
                            )
                        ).setLinearHeadingInterpolation(- PI / 2, 0.0)
                        .build()
                )
            )
            andThen FollowPedroPath(
                PathBuilder()
                    .addPath(
                        BezierLine(
                            Point(13.2, 60.0),
                            Point(36.0, 66.0),
                        )
                    ).setConstantHeadingInterpolation(0.0)
                    .build()
            )
            andThen ( OuttakeSlides.run { it.setPower(-0.5) } withTimeout(0.5) )
            andThen ( OuttakeSlides.runOnce { it.setPower(0.0) } )
            andThen Claw.release()
        )

        var lastTime = System.nanoTime()
        var currentTime = System.nanoTime()
        RunCommand {
            lastTime = currentTime
            currentTime = System.nanoTime()
        }.schedule()

        (
            placePreload
            andThen cycleHumanPlayer
            andThen cycleBar
            andThen cycleHumanPlayer
            andThen cycleBar
        ).schedule()

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("hertz") { floor(1 / ((currentTime - lastTime) * 1e-9)) }
        Telemetry.addFunction("pos") { Drivetrain.pos }

        Telemetry.addFunction("slides") { OuttakeSlides.position }
        Telemetry.addFunction("delta") { OuttakeSlides.leftMotor.encoder!!.delta }
        Telemetry.addFunction("power") { OuttakeSlides.leftMotor.lastWrite}
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }
}
