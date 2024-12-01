package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.FollowPedroPath
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.pedroPathing.HeadingType
import org.ftc3825.pedroPathing.followPath
import org.ftc3825.pedroPathing.path
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
        Drivetrain.position = Pose2D(8.0, 66.0, 0.0)
        Command.parallel(
            Arm.pitchUp(),
            Claw.pitchDown(),
            Claw.grab()
        ).schedule()

        val path1 = path {
            start(8, 66)
            lineTo(37, 64, HeadingType.constant(0.0))
        }

        val placePreload = (
            OuttakeSlides.runToPosition(440.0)
            parallelTo ( FollowPedroPath(path1) )
            andThen (
                OuttakeSlides.run { it.setPower(0.6) }
                    withEnd { OuttakeSlides.setPower(0.0) }
                    withTimeout(0.5)
            )
            andThen Claw.release()
        )
        val moveFieldSpecimens = (
            followPath {
                start(23, 66)
                curveTo(
                    25, 35,
                    45, 35,
                    HeadingType.constant(0.0)
                )
                curveTo(
                    60, 35,
                    60, 25,
                    HeadingType.constant(0.0)
                )
                pathBuilder.setPathEndTranslationalConstraint(0.5)
                pathBuilder.setPathEndTValueConstraint(0.98)
            } andThen followPath {
                start(60, 24)
                lineTo(21, 24, HeadingType.constant(0.0))
            } andThen followPath {
                start(21, 24)
                lineTo(23, 35, HeadingType.constant(0.0))
            }
        )
        fun cycleHumanPlayer(deposit: Double) = (
            OuttakeSlides.runToPosition(200.0)
            parallelTo (
                followPath {
                   start(36, deposit)
                   lineTo(12, 60, HeadingType.linear(0.0, - PI / 2))
                }
            )
            andThen Command.parallel(
                Arm.pitchDown(),
                Claw.groundSpecimenPitch(),
                Claw.release(),
                followPath {
                    start(12, 60)
                    lineTo(12, 49, HeadingType.constant(- PI / 2))
                    pathBuilder.setPathEndTranslationalConstraint(0.05)
                }
            )
            andThen ( OuttakeSlides.retract() withTimeout(0.5) )
            andThen ( Claw.grab() parallelTo WaitCommand(0.5) )
            andThen ( Arm.pitchUp() parallelTo Claw.pitchDown() )
        )

        fun cycleBar(deposit: Double) = (
            OuttakeSlides.runToPosition(400.0)
            parallelTo (
                followPath {
                    start(13, 48)
                    lineTo(13, 60, HeadingType.linear(- PI / 2, 0.0))
                    lineTo(36, deposit, HeadingType.constant(0.0))
                }
            )
            andThen ( OuttakeSlides.run { it.setPower(-0.4) } withTimeout(0.3) )
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
            andThen moveFieldSpecimens
            andThen cycleHumanPlayer(64.0)
            andThen cycleBar(67.0)
            andThen cycleHumanPlayer(67.0)
            andThen cycleBar(70.0)
        ).schedule()

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("hertz") { floor(1 / ((currentTime - lastTime) * 1e-9)) }
        Telemetry.addFunction("position") { Drivetrain.position }

        //Telemetry.addFunction("path") { Drivetrain.currentPath ?: "no path"}
        Telemetry.addFunction("isBusy") { Drivetrain.isFollowing }

        Telemetry.addFunction("slides") { OuttakeSlides.position }
        Telemetry.addFunction("delta") { OuttakeSlides.leftMotor.encoder!!.delta }
        Telemetry.addFunction("power") { OuttakeSlides.leftMotor.lastWrite}
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }
}
