package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.intake
import org.firstinspires.ftc.teamcode.command.shoot
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.GPP
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import org.firstinspires.ftc.teamcode.util.SelectorInput
import kotlin.math.PI

@Autonomous
class Auto15Ball: CommandOpMode() {
    var startBack by SelectorInput("start in back", false, true)
    var usePreloads by SelectorInput("start in back", true, false)

    override fun preSelector() {
        Globals
        TankDrivetrain.resetLocalizer()
    }

    override fun postSelector() {
        if(startBack) usePreloads = false

        val xMul       = if (Globals.alliance == BLUE) 1    else -1
        val startPose  = if(startBack){
            Pose2D(-5 * xMul, -62, 3 * PI / 2)
        } else {
            if (Globals.alliance == BLUE) {
                Pose2D(-50.5, 53.0, -0.838 + 2 * PI)
            }
            else Pose2D(50.3, 54.6, 3.8)
        }
        TankDrivetrain.position = startPose

        val cycle1 =  (
            intake(
                 followPath {
                    start(-12 * xMul, 12)
                    lineTo(-56 * xMul, 12, tangent)
                }.withConstraints(velConstraint = 3.0)
            )
            andThen shoot(
                followPath {
                    start(-56 * xMul, 12)
                    lineTo(
                        -12 * xMul, 12,
                        reverseTangent
                    )
                }
            )
        )
        val cycle2 = (
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
            andThen ( followPath {
                start(-38 * xMul, -12)
                curveTo(
                    -5 * xMul, 2.5,
                    -10 * xMul, 0,
                    -58 * xMul, -2,
                    tangent
                )
            } withTimeout(1) )
            andThen WaitCommand(1.0)
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
        )
        val cycle3 = (
            intake(
                followPath {
                    start(-12 * xMul, 12)
                    curveTo(
                        0, -30,
                        -30 * xMul, 0,
                        -56 * xMul, -36,
                        tangent
                    )
                }.withConstraints(
                    velConstraint = 3.0,
                    maxVel = 40.0,
                    dMax = 40.0

                )
            )
            andThen shoot(
                followPath {
                    start(-56 * xMul, -36)
                    curveTo(
                        10 * xMul, 0,
                        0, 30,
                        -12 * xMul, 12,
                        reverseTangent
                    )
                }.withConstraints(
                    aMax = 50.0,
                    maxVel = 50.0
                )
            )
        )
        val cycleHP =  (
            intake(
                followPath {
                    start(-29 * xMul, 29)
                    curveTo(
                        -10*xMul, -10,
                        0, -20,
                        -67, -60,
                        tangent
                    )
                }.withConstraints(velConstraint = 3.0)
            )
            andThen shoot(
                followPath {
                    start(-67 * xMul, -60)
                    curveTo(
                        0, 20 * xMul,
                        20 * xMul, 20,
                        -12 * xMul, 12,
                        reverseTangent
                    )
                }
            )
        )
        val cyclePreloads = (
            intake(
                followPath {
                    start(-12 * xMul, 12)
                    lineTo(-12 * xMul, -62, tangent)
                }
            )
            andThen shoot(
                followPath {
                    start(-12, -62)
                    lineTo(-12 * xMul, 28, reverseTangent)
                }
            )
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
                If({Globals.randomization == GPP },
                            cycle1
                    andThen cycle2
                    andThen cycle3
                )
                Else (
                            cycle3
                    andThen cycle2
                    andThen cycle1
                )
            )
            andThen (
                If({usePreloads}, cyclePreloads)
                Else cycleHP
            )
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}