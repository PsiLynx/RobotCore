package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.intake
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.shoot
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.gvf.Arc
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.GPP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PGP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PPG
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.PI

@Autonomous
class Auto12Sorted: CommandOpMode() {

    override fun preSelector() {
        Globals
        TankDrivetrain.resetLocalizer()
    }

    override fun postSelector() {

        val xMul       = if (Globals.alliance == BLUE) 1    else -1
        val startPose  = (
            if (Globals.alliance == BLUE) {
                Pose2D(-50.5, 53.0, -0.838 + 2 * PI)
            }
            else Pose2D(50.3, 54.6, 3.8)
        )
        Globals.randomization = PGP
        //TODO: REMOVE THIS
        TankDrivetrain.position = startPose

        fun cyclePPG() = (
            // cycle 1
            intake(
                 followPath {
                    start(-12 * xMul, 12)
                    lineTo(-56 * xMul, 12, tangent)
                }.withConstraints(velConstraint = 3.0)
            )
            andThen ( TankDrivetrain.power(-0.5) withTimeout 0.6 )
            andThen ( followPath {
                start(-38 * xMul, 12)
                curveTo(
                    -5 * xMul, -2.5,
                    -10 * xMul, 0,
                    -58 * xMul, 2,
                    tangent
                )
            } withTimeout(1) )
            andThen WaitCommand(0.5)
            andThen shoot(
                followPath {
                    start(-56 * xMul, 2)
                    curveTo(
                        10 * xMul, 0,
                        10 * xMul, 10,
                        -12 * xMul, 12,
                        reverseTangent
                    )
                }
            )

            // cycle 2
            andThen intake(
                followPath {
                    start(-12 * xMul, 12)
                    curveTo(
                        -10 * xMul, -10,
                        -10 * xMul, 0,
                        -36 * xMul, -12,
                        tangent
                    )
                }.withConstraints(dMax = 30.0)
            )
            andThen followPath {
                start(-36 * xMul, -12)
                lastTangent = Vector2D(xMul, 0)
                arc(
                    Arc.Direction.RIGHT * xMul,
                    degrees(160),
                    6,
                    reverseTangent
                )
                endVel(0.1)
                lineTo(-65, -24, reverseTangent)
            }.withConstraints(
                aMax = 20.0,
            )
            andThen intake(
                followPath {
                    start(-65 * xMul, -26)
                    curveTo(
                        10 * xMul, 10,
                        20 * xMul, 0,
                        -36 * xMul, -12,
                        tangent
                    )
                    endVel(0.5)
                }
            )
            andThen shoot(
                followPath {
                    start(-36 * xMul, -12)
                    endVel(0.5)
                    curveTo(
                        20 * xMul, 0,
                        0, 10,
                        -12 * xMul, 12,
                        tangent
                    )
                }
            )

            // cycle 3
            andThen intake(
                followPath {
                    start(-12 * xMul, 12)


                    curveTo(
                        -10 * xMul, -10,
                        -20 * xMul, 0,
                        -40 * xMul, -12,
                        tangent
                    )
                    endVel(0.4)
                    curveTo(
                        -10 * xMul, 0,
                        0, -10,
                        -60 * xMul, -24,
                        tangent
                    )
                    curveTo(
                        0, -10,
                        20 * xMul, 0,
                        -45 * xMul, -36,
                        tangent
                    )
                    endVel(0.5)
                }.withConstraints(
                    maxVel = 40.0,
                    posConstraint = 4.0
                )
            )
            andThen shoot(
                followPath {
                    endVel(0.5)
                    start(-40 * xMul, -36)
                    // this is intentionally behind where the other one ends,
                    // the other path will end prematurely due to it not
                    // needing to stop
                    curveTo(
                        20 * xMul, 0,
                        0, 10,
                        -12 * xMul, 28,
                        tangent
                    )
                }
            )
        )
        fun cyclePGP() = (
            // cycle 2
            intake(
                followPath {
                    start(-12 * xMul, 12)
                    curveTo(
                        -10*xMul, -15,
                        -35*xMul, 0,
                        -58*xMul, -12,
                        tangent
                    )
                }.withConstraints(
                    dMax = 40.0,
                    maxVel = 40.0,
                    velConstraint = 3.0,
                )
            )
            andThen ( TankDrivetrain.power(-0.5) withTimeout 0.6 )
            andThen (
                followPath {
                    start(-38 * xMul, -12)
                    curveTo(
                        -5 * xMul, 2.5,
                        -10 * xMul, 0,
                        -58 * xMul, -2,
                        tangent
                    )
                } withTimeout(1)
            )
            andThen WaitCommand(0.5)
            andThen shoot(
                followPath {
                    start(-60 * xMul, -6)
                    curveTo(
                        10 * xMul, -5,
                        10 * xMul, 20,
                        -12 * xMul, 12,
                        reverseTangent
                    )
                }
            )

            // cycle 1
            andThen intake(
                followPath {
                    start(-12 * xMul, 12)
                    curveTo(
                        -30 * xMul, -30,
                        -7 * xMul, 3,
                        -60 * xMul, 18,
                        tangent
                    )
                }
            )
            andThen followPath {
                start(-60 * xMul, 18)
                lineTo(-24, 20, reverseTangent)
                endVel(0.0)
                lineTo(-42 * xMul, 12, tangent)
            }
            andThen shoot(
                followPath {
                    start(-42 * xMul, 12)
                    lineTo(-12 * xMul, 12, reverseTangent)
                }
            )

            // cycle 3
            andThen intake(
                followPath {
                    start(-12 * xMul, 12)
                    curveTo(
                        -10*xMul, -5,
                        0, -20,
                        -63 * xMul, -22,
                        tangent
                    )
                    endVel(0.2)
                    curveTo(
                        0, -5,
                        4 * xMul, -5,
                        -57 * xMul, -34,
                        tangent
                    )
                }.withConstraints(dMax = 30.0)
            )
            andThen followPath {
                start(-59 * xMul, -36)
                lastTangent = Vector2D(-xMul, 0)
                arc(
                    Arc.Direction.RIGHT * xMul,
                    degrees(160),
                    6,
                    reverseTangent
                )
                endVel(0.1)
                lineTo(-24 * xMul, -24, reverseTangent)
            }
                andThen intake(
                    headingLock = false,
                    pathCommand = followPath {
                        start(-24 * xMul, -24)
                        curveTo(
                            -10 * xMul, -10,
                            -20 * xMul, 0,
                            -55 * xMul, -36,
                            tangent
                        )
                    }
                )
                andThen shoot(
                    followPath {
                        start(-55 * xMul, -36)
                        curveTo(
                            10 * xMul, 0,
                            0, 10,
                            -12 * xMul, 28,
                            reverseTangent
                        )
                    }
                )
        )
        fun cycleGPP() = (
            Command()
        )
        val auto = (
            WaitCommand(0.01)
            andThen (
                (
                    WaitCommand(0.3)
                    andThen ShootingStateOTM()
                ) racesWith (
                    (
                        followPath {
                            start(startPose.vector)
                            lineTo(-12 * xMul, 12, tangent)
                        }
                    )
                    parallelTo (
                        WaitUntilCommand(Robot::readyToShoot) withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )

            )
            andThen (
                If({Globals.randomization == PPG }, cyclePPG())
                .ElseIf({Globals.randomization == GPP }, cycleGPP())
                .ElseIf ({Globals.randomization == PGP }, cyclePGP())
            )
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}