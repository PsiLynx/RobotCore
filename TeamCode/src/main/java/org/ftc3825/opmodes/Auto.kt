package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.pedroPathing.HeadingType
import org.ftc3825.pedroPathing.followPath
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Globals
import org.ftc3825.util.Pose2D
import kotlin.math.PI
import kotlin.math.floor


@Autonomous(name = "3+0")
class Auto: CommandOpMode() {
    override fun init() {
        initialize()

        Arm.reset()
        Claw.reset()
        Drivetrain.reset()
        OuttakeSlides.reset()

        Globals.AUTO = true
        Drivetrain.position = Pose2D(8.0, 66.0, 0.0)
        Drivetrain.update(0.1)
        Claw.justUpdate().schedule()

        Command.parallel(
            Arm.runOnce { it.pitchServo.position = 1.0; it.pitchServo.position = 0.0 },
            Claw.runOnce { it.gripServo.position = 1.0; it.gripServo.position = 0.7 },
            Claw.rollCenter(),
            Claw.pitchDown(),
        ).schedule()

        val placePreload = (
            OuttakeSlides.holdPosition(440.0) racesWith followPath {
                start(8, 66)
                lineTo(37, 64, HeadingType.constant(0.0))
            }
            andThen OuttakeSlides.powerForTime(0.7, seconds = 0.5)
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
            }
                andThen followPath {
                start(60, 24)
                lineTo(21, 24, HeadingType.constant(0.0))
                curveTo(
                    40, 24,
                    23,24,
                    HeadingType.linear(0.0, PI)
                )
            }
        )
        val intake = (
            Claw.release()
            andThen Arm.wallPitch()
            andThen OuttakeSlides.retract()
            andThen Claw.grab()
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
            andThen intake
            andThen (
                followPath {
                    start(23, 24)
                    lineTo(37, 61, HeadingType.linear(PI, 0.0)) //to bar
                } parallelTo OuttakeSlides.runToPosition(400.0)
            )
            andThen OuttakeSlides.powerForTime(-0.5, seconds = 0.5) //hang
            andThen Claw.release()
            andThen Command.parallel(
                followPath {
                    start(37, 61)
                    lineTo(25, 24, HeadingType.linear(0.0, PI))
                    lineTo(23, 24, HeadingType.constant(PI)) //intake
                },
                OuttakeSlides.retract(),
                Arm.wallPitch()
            )
            andThen intake
            andThen (
                followPath {
                    start(21, 24)
                    lineTo(37, 58, HeadingType.linear(PI, 0.0)) //to bar
                } parallelTo OuttakeSlides.runToPosition(400.0)
            )
            andThen OuttakeSlides.powerForTime(-0.5, seconds = 0.5)
            andThen (
                followPath {
                    start(37, 58)
                    lineTo(12, 12, HeadingType.constant(0.0)) //park
                } parallelTo OuttakeSlides.retract()
            )

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
        Telemetry.update(0.1)
    }
}
