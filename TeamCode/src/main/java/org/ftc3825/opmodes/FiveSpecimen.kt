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
            Point(25.0, startY.toDouble()),
            Point(60.0, startY.toDouble()),
            Point(60.0, (endY.toDouble() + startY.toDouble()) / 2)
        )
    ).setConstantHeadingInterpolation(0.0)
        .addPath(
            BezierCurve(
                Point(60.0, (endY.toDouble() + startY.toDouble()) / 2),
                Point(60.0, endY.toDouble()),
                Point(25.0, endY.toDouble())
            )
        ).setConstantHeadingInterpolation(0.0)

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
                    Point(8.0, 66.0),
                    Point(23.0, 66.0)
                )
            ).setPathEndTimeoutConstraint(100.0)
            .setConstantHeadingInterpolation(0.0)
            .build()



        var placePreload = (
            OuttakeSlides.holdPosition(1090.0)
                parallelTo (
                    InstantCommand {
                        Arm.pitchDown()
                        Claw.pitchUp()
                        Claw.grab()
                        Claw.rollCenter()
                    }
                    andThen WaitCommand(1)
                    andThen ( FollowPedroPath(path1) withTimeout(2) )
                    andThen OuttakeSlides.breakHolding()
                    andThen ( OuttakeSlides.run { it.leftMotor.doNotFeedback(); it.setPower(1.0) } withTimeout(1) )
                    andThen InstantCommand {
                        Claw.release()
                        Arm.pitchUp()
                    }
                    andThen OuttakeSlides.retract()
                )
        )

        var moveFieldSpecimens = ( /*OuttakeSlides.retract() andThen */ InstantCommand { } ) parallelTo (
            FollowPedroPath(
                PathBuilder()
                    .addPath(
                        BezierCurve(
                            Point(23.0, 66.0),
                            Point(23.0, 37.0),
                            Point(54.0, 37.0)
                        )
                    ).setConstantHeadingInterpolation(0.0)
                    .addPath(
                        BezierCurve(
                            Point(54.0, 37.0),
                            Point(60.0, 37.0),
                            Point(60.0, 26.0)
                        )
                    ).setConstantHeadingInterpolation(0.0)
                    .setZeroPowerAccelerationMultiplier(10.0)
                    .build()
            ) andThen FollowPedroPath(
                PathBuilder()
                    .addPath(
                        BezierLine(
                            Point(60.0, 26.0),
                            Point(23.0, 26.0)
                        )
                    ).setConstantHeadingInterpolation(0.0)
                    .build()
            )
        ) andThen InstantCommand { Drivetrain.follower.setMaxPower(1.0) }

        var moveToCycle = (
            OuttakeSlides.runToPosition(450.0)
            andThen (
                FollowPedroPath(
                    PathBuilder()
                        .addPath(
                            BezierLine(
                                Point(23.0, 26.0),
                                Point(33.0, 35.0)
                            )
                        ).setLinearHeadingInterpolation(0.0, PI)
                        .build()
                )
                andThen (
                    InstantCommand {
                        Arm.pitchDown()
                        Claw.pitchUp()
                        Claw.release()
                    } parallelTo WaitCommand(1)
                )

            )
        )

        var cycle = (
            Drivetrain.run { it.setWeightedDrivePower(0.3, 0.0, 0.0) } withTimeout(1)
            andThen Drivetrain.runOnce { it.setWeightedDrivePower(Pose2D()) }
            andThen InstantCommand { Claw.grab() }
            andThen WaitCommand(0.5)
            andThen
            InstantCommand {
                Arm.pitchUp()
                Claw.pitchUp()
            }
            andThen OuttakeSlides.runToPosition(700.0)
            andThen FollowPedroPath(
                    PathBuilder()
                        .addPath(
                            BezierLine(
                                Point(36.5, 35.0),
                                Point(36.8, 66.0)
                            )
                        ).setLinearHeadingInterpolation(PI, 0.0)
                        .build()
            )
            andThen ( OuttakeSlides.run { it.leftMotor.doNotFeedback(); it.setPower(-0.5) } withTimeout(1) )
            andThen ( InstantCommand { Claw.release() } parallelTo OuttakeSlides.runOnce { it.setPower(0.0) } )
            andThen WaitCommand(0.5)
            andThen (
                OuttakeSlides.runToPosition(450.0)
                parallelTo FollowPedroPath(
                    PathBuilder()
                        .addPath(
                            BezierLine(
                                Point(22.0, 66.0),
                                Point(36.5, 35.0)
                            )
                        ).setLinearHeadingInterpolation(0.0, PI)
                        .build()
                )
            )
            andThen (
                InstantCommand {
                    Arm.pitchDown()
                    Claw.pitchUp()
                    Claw.release()
                } parallelTo WaitCommand(1)
            )
        )
        val park = FollowPedroPath(
            PathBuilder()
                .addPath(
                    BezierLine(
                        Point(36.5, 35.0),
                        Point(26.5, 35.5)
                    )
                )
                .build()
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
                andThen park

            ).schedule()

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("hertz") { floor(1 / ((currentTime - lastTime) * 1e-9)) }
        Telemetry.addFunction("") { Drivetrain.follower.pose }
        Telemetry.addFunction("slides") { OuttakeSlides.position }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }
}
