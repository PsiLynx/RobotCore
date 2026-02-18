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
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import org.firstinspires.ftc.teamcode.util.SelectorInput
import kotlin.math.PI

@Autonomous
class Auto12Ball: CommandOpMode() {
    var startBack by SelectorInput("start in back", false, true)
    var hitGate by SelectorInput("hit gate", false, true)

    override fun preSelector() {
        Globals
        TankDrivetrain.resetLocalizer()
    }

    override fun postSelector() {
        val xMul       = if (Globals.alliance == BLUE) 1    else -1
        val startPose  = if(startBack){
            Pose2D(-5 * xMul, -62, 3 * PI / 2)
        } else {
            if (Globals.alliance == BLUE) {
                Pose2D(-50.8, 53.4, -0.838 + 2 * PI)
            }
            else Pose2D(51.25, 53.8, 3.8)
        }
        TankDrivetrain.position = startPose

        val cycle1 =  (
            intake(
                 followPath {
                    start(-28 * xMul, 28)
                    curveTo(
                        0, -5,
                        -35 * xMul, 0,
                        -56 * xMul, 12,
                        tangent
                    )
                }.withConstraints(
                     velConstraint = 3.0,
                     dMax = 30.0
                )
            )
            andThen shoot(
                followPath {
                    start(-56 * xMul, 12)
                    curveTo(
                        10 * xMul, 0,
                        0, 10,
                        -28 * xMul, 42,
                        reverseTangent
                    )
                }
            )
        )
        val cycle2 = (
            intake(
                followPath {
                    start(-28 * xMul, 28)
                    curveTo(
                        -4*xMul, -10,
                        -40*xMul, 0,
                        -58*xMul, -12,
                        tangent
                    )
                }.withConstraints(
                    dMax = 40.0,
                    maxVel = 40.0,
                    velConstraint = 3.0,
                )
            )
            andThen If({hitGate},(
                ( TankDrivetrain.power(-0.5) withTimeout 0.4 )
                andThen ( followPath {
                    start(-38 * xMul, -12)
                    curveTo(
                        -5 * xMul, 2.5,
                        -10 * xMul, 0,
                        -58 * xMul, -2,
                        tangent
                    )
                } withTimeout(1) )
                andThen WaitCommand(0.6)
            ))
            andThen shoot(
                followPath {
                    start(-60 * xMul, -6)
                    curveTo(
                        25 * xMul, -3,
                        0, 15,
                        -28 * xMul, 28,
                        reverseTangent
                    )
                }
            )
        )
        val cycle3 = (
            intake(
                followPath {
                    start(-28 * xMul, 28)
                    curveTo(
                        0, -30,
                        -50 * xMul, 0,
                        -58 * xMul, -36,
                        tangent
                    )
                }.withConstraints(
                    velConstraint = 3.0,
                    maxVel = 40.0,
                    dMax = 30.0

                )
            )
            andThen shoot(
                followPath {
                    start(-58 * xMul, -36)
                    curveTo(
                        20 * xMul, 0,
                        0, 30,
                        -28 * xMul, 28,
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
        )
        val cyclePreloads = (
            intake(
                followPath {
                    start(-28 * xMul, 28)
                    curveTo(
                        0, -10,
                        0, -60,
                        -12 * xMul, -62,
                        tangent
                    )
                }
            )
            andThen shoot(
                followPath {
                    start(-12, -62)
                    lineTo(-28 * xMul, 36, reverseTangent)
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
                            lineTo(-28 * xMul, 28, tangent)
                        }
                    )
                    andThen (
                        WaitUntilCommand(Robot::readyToShoot) withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )

            )
            andThen cycle2
            andThen cycle3
            andThen cycle1
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}