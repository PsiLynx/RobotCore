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
//    override fun init_loop() {
//        Arm.pitchServo.position = 1.0
//    }
    override fun init() {
        initialize()

        Arm.reset()
        Claw.reset()
        Drivetrain.reset()
        OuttakeSlides.reset()

        Globals.AUTO = true
        Drivetrain.position = Pose2D(8.0, 66.0, 0.0)
        Claw.justUpdate().schedule()
        Command.parallel(
            Arm.runOnce { it.pitchServo.position = 1.0; it.pitchServo.position = 0.0 },
            Claw.runOnce { it.gripServo.position = 1.0; it.gripServo.position = 0.7 },
            Claw.rollCenter(),
            Claw.pitchDown(),
        ).schedule()

        val path1 = path {
            start(8, 66)
            lineTo(37, 64, HeadingType.constant(0.0))
        }

        val placePreload = (
            OuttakeSlides.run { it.setPower(0.01) } withTimeout(0.1)
            andThen ( OuttakeSlides.runToPosition(440.0) withTimeout(2) )
            andThen ( FollowPedroPath(path1) )
            andThen (
                OuttakeSlides.run { it.setPower(0.7) }
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
            } andThen followPath {
                start(60, 24)
                lineTo(21, 24, HeadingType.constant(0.0))
            } andThen followPath {
                start(21, 24)
                lineTo(20, 50, HeadingType.linear(0.0, - PI / 2))
            }
        )
        fun cycleHumanPlayer(deposit: Double) = (
            ( OuttakeSlides.runToPosition(300.0) withTimeout(2) )
            andThen (
                followPath {
                   start(20, deposit)
                   lineTo(7.8, 60, HeadingType.constant(- PI / 2))
                } withTimeout(2)
            )
            parallelTo (
                WaitCommand(1)
                andThen Command.parallel(
                    Arm.pitchDown(),
                    Claw.groundSpecimenPitch(),
                    Claw.release()
                )
            )
            andThen WaitCommand(1)
            andThen (
                followPath {
                    start(7.8, 60)
                    lineTo(7.8, 47.75, HeadingType.constant(- PI / 2))
                    pathBuilder.setPathEndTranslationalConstraint(0.2)
                } withTimeout(2)
            )
            andThen ( OuttakeSlides.retract() withTimeout(0.5) )
            andThen ( Claw.grab() parallelTo WaitCommand(1.0) )
            andThen ( Arm.pitchUp() parallelTo Claw.pitchDown() )
        )
    fun park(deposit: Double) = (
        followPath {
            start(20, deposit)
            lineTo(18, 60, HeadingType.constant(0.0))
            lineTo(12, 20, HeadingType.constant(0.0))
        }
    )

    fun cycleBar(deposit: Double) = (
            OuttakeSlides.runToPosition(400.0)
            parallelTo (
                followPath {
                    start(13, 47.75)
                    lineTo(13, 60, HeadingType.linear(- PI / 2, 0.0))
                    lineTo(36.4, deposit, HeadingType.constant(0.0))
                } withTimeout(3)
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
            andThen cycleBar(67.0) // 67
            andThen cycleHumanPlayer(67.0) // 67
            andThen cycleBar(70.0) // 70
            andThen park(70.0) // 70
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
