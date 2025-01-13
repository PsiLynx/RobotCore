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


@Autonomous(name = "4+0")
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
                lineTo(36.8, 64, HeadingType.constant(0.0))
            }
            andThen(
                OuttakeSlides.run { OuttakeSlides.setPower(1.0) }
                withTimeout(0.3)
                withEnd { OuttakeSlides.setPower(0.0) }
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
                    58, 35,
                    58, 25,
                    HeadingType.constant(0.0)
                )
            } parallelTo OuttakeSlides.retract()
            andThen followPath {
                start(58, 28) //behind first
                lineTo(25, 28, HeadingType.constant(0.0))
                lineTo(61, 28, HeadingType.constant(0.0))
                pathBuilder.setPathEndTranslationalConstraint(0.5)
                pathBuilder.setPathEndTValueConstraint(0.8)
                pathBuilder.setPathEndHeadingConstraint(0.1)
                pathBuilder.setPathEndTimeoutConstraint(0.0)
            }
            andThen followPath {
                start(58, 24)
                lineTo(58, 16, HeadingType.constant(0.0)) //behind second
            }
            andThen followPath {
                start(58, 16)
                lineTo(16, 16, HeadingType.constant(0.0))
                pathBuilder.setPathEndTranslationalConstraint(0.5)
                pathBuilder.setPathEndTValueConstraint(0.8)
                pathBuilder.setPathEndHeadingConstraint(0.1)
                pathBuilder.setPathEndTimeoutConstraint(0.0)
            }
            andThen Arm.wallPitch()
            andThen Claw.wallPitch()
            andThen followPath {
                start(24, 16)
                lineTo(29, 24, HeadingType.constant(PI / 2))
                lineTo(38, 30, HeadingType.constant(PI))
            }
            andThen ( followPath {
                start(38, 30)
                lineTo(25, 30, HeadingType.constant(PI))
                //pathBuilder.setZeroPowerAccelerationMultiplier(3.0)
            } withTimeout(2) )
        )
        val intake = (
            Claw.release()
            andThen Arm.wallPitch()
            andThen Claw.wallPitch()
            andThen OuttakeSlides.retract()
            andThen Claw.grab()
            andThen WaitCommand(0.2)
            andThen OuttakeSlides.runToPosition(400.0)
            andThen Arm.pitchUp()
            andThen Claw.pitchDown()
        )
        fun cycle(barPos: Double) = (
            intake
            andThen (
                followPath {
                    start(24, 24)
                    lineTo(30, barPos, HeadingType.linear(PI, 0.0))
                    lineTo(36.8, barPos, HeadingType.constant(0.0)) //to bar
                } racesWith ( OuttakeSlides.runToPosition(400.0) andThen RunCommand { } )
            )
            andThen OuttakeSlides.powerForTime(-0.5, seconds = 0.3) //hang
            andThen Claw.release()
            andThen Command.parallel (
                followPath {
                    start(36.8, barPos)
                    lineTo(34, 45, HeadingType.constant(PI / 2))
                    lineTo(38, 30, HeadingType.constant(PI))
                },
                OuttakeSlides.retract(),
                Arm.wallPitch(),
                Claw.wallPitch(),
            )
            andThen ( followPath {
                start(38, 30)
                lineTo(25, 30, HeadingType.constant(PI))
                //pathBuilder.setZeroPowerAccelerationMultiplier(3.0)
            } withTimeout(2) )
        )


        var lastTime = System.nanoTime()
        var currentTime = System.nanoTime()
        RunCommand {
            lastTime = currentTime
            currentTime = System.nanoTime()
        }.schedule()

        (
            WaitCommand(0.1)
            andThen placePreload
            andThen moveFieldSpecimens
            andThen cycle(67.0)
            andThen cycle(70.0) //TODO: CHANGE!!
            andThen intake
            andThen (
                followPath {
                    start(21, 24)
                    lineTo(30, 73, HeadingType.linear(PI, 0.0))
                    lineTo(36.8, 73, HeadingType.constant(0.0)) //to bar
                } parallelTo OuttakeSlides.runToPosition(400.0)
                withTimeout(3)
            )
            andThen OuttakeSlides.powerForTime(-0.5, seconds = 0.5)
            andThen Claw.release()
            andThen (
                followPath {
                    start(36.8, 58)
                    lineTo(12, 24, HeadingType.constant(0.0)) //park
                } parallelTo OuttakeSlides.retract()
            )

        ).schedule()

        Telemetry.addAll {
            "hertz"    ids { floor(1 / ((currentTime - lastTime) * 1e-9)) }
            "position" ids { Drivetrain.position }

            "path"     ids { Drivetrain.currentPath ?: "no path"}
            "isBusy"   ids { Drivetrain.isFollowing }

            "slides"   ids { OuttakeSlides.position }
            "delta"    ids { OuttakeSlides.leftMotor.encoder!!.delta }
            "power"    ids { OuttakeSlides.leftMotor.lastWrite }
            newLine()
            ""       ids { CommandScheduler.status() }
        }
    }
}
