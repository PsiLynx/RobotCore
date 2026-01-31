package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.gvf.Arc
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.Telemetry.ids
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

@Autonomous
class AutoFront: CommandOpMode() {
    override fun preSelector() {
        Globals
        TankDrivetrain.resetLocalizer()
    }
    override fun postSelector() {
        val xMul       = if (Globals.alliance == BLUE) 1    else -1

        val startPose = if (Globals.alliance == BLUE) {
            Pose2D(
                -50.5, 53.38, -0.734 + 2*PI
            )
        } else Pose2D(
            51.5, 50.8, PI/2 - degrees(50) + PI
        )
        val grabPreloads by SelectorInput("grab preloads", false, true)
        TankDrivetrain.position = startPose

        fun cycle1() = (
            (
                Intake.run()
                racesWith followPath {
                    start(-12 * xMul, 12)
                    lineTo(-50 * xMul, 12, tangent)
                }.withConstraints(velConstraint = 3.0)
            )
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    followPath {
                        start(-50 * xMul, 12)
                        lineTo(
                            -12 * xMul, 12,
                            reverseTangent
                        )
                    }
                    andThen (
                        (
                            WaitUntilCommand(Robot::readyToShoot)
                            withTimeout 1
                            andThen Robot.kickBalls()
                        )
                        racesWith (
                            TankDrivetrain.power(0.0, 0.2) withTimeout 0.7
                            andThen TankDrivetrain.headingLock(
                                -PI/2
                                -PI/4*xMul
                            )
                        )
                    )
                )
            )
        )
        fun cycle2() = (
            (
                Intake.run()
                racesWith (
                    followPath {
                        start(-12 * xMul, 12)
                        curveTo(
                            -5, -5,
                            -30*xMul, 0,
                            -52*xMul, -12,
                            tangent
                        )
                    }.withConstraints(velConstraint = 3.0)
                    andThen followPath {
                        start(-52 * xMul, -12)
                        lineTo(-38 * xMul, -12, reverseTangent)
                    }.withConstraints(
                        velConstraint = 5.0
                    )
                )
            )
            andThen ( followPath {
                start(-38 * xMul, -12)
                lastTangent = Vector2D(-1 * xMul, 0.5)
                curveTo(
                    -5 * xMul, 2.5,
                    -10 * xMul, 0,
                    -60 * xMul, -1,
                    tangent
                )
            } withTimeout(1) )
            andThen (
                ShootingStateOTM()
                racesWith (
                    WaitCommand(0.3)
                    andThen followPath {
                        start(-60 * xMul, -1)
                        curveTo(
                            10 * xMul, 0,
                            10 * xMul, 10,
                            -12 * xMul, 12,
                            reverseTangent
                        )
                    }
                    andThen (
                        WaitUntilCommand(Robot::readyToShoot)
                        withTimeout 1
                        andThen Robot.kickBalls()
                        racesWith (
                            TankDrivetrain.power(0.0, 0.2) withTimeout 0.7
                            andThen TankDrivetrain.headingLock(3 * PI / 2)
                        )
                    )
                )
            )
        )

        fun cycle3() = (
            (
                Intake.run()
                racesWith followPath {
                    start(-12 * xMul, 12)
                    curveTo(
                        0, -30,
                        -40 * xMul, 0,
                        -52 * xMul, -36,
                        tangent
                    )
                }.withConstraints(velConstraint = 3.0)
            )
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    (
                        (
                            If({grabPreloads}, followPath {
                                start(-52 * xMul, -36)
                                curveTo(
                                    40*xMul, 0,
                                    0, 30,
                                    -12*xMul, 6,
                                    reverseTangent
                                )
                            }) Else followPath {
                                start(-52 * xMul, -36)
                                curveTo(
                                    20*xMul, 0,
                                    20*xMul, 20,
                                    -12*xMul, 6,
                                    reverseTangent
                                )
                            }
                        )
                        racesWith Intake.run(motorPow = 0.5)
                    )
                    andThen (
                        (
                            WaitUntilCommand(Robot::readyToShoot)
                            withTimeout 1
                            andThen Robot.kickBalls()
                        )
                    )
                )
            )
        )
        fun cyclePreloads() = (
                Intake.run() racesWith followPath {
                    start(-12 * xMul, 6)
                    lineTo(-12 * xMul, -60, tangent)
                }
                andThen (
                    ShootingStateOTM() racesWith (
                        followPath {
                            start(-12, -60)
                            lineTo(-12 * xMul, 28, reverseTangent)
                            endVel(0.3)
                        }
                        andThen (
                            (
                                WaitUntilCommand(Robot::readyToShoot)
                                withTimeout 1
                                andThen Robot.kickBalls()
                            ) /*parallelTo (
                                TankDrivetrain.power(-0.3)
                                until { TankDrivetrain.position.y > 28 }
                            )*/
                        )
                    )
                )
        )
        fun cycleHP() = (
            Intake.run() racesWith followPath {
                start(-12 * xMul, 6)
                curveTo(
                    -20*xMul, -20,
                    0, -30,
                    -67*xMul, -62,
                    tangent
                )
            }
            andThen (
                ShootingStateOTM() racesWith (
                    followPath {
                        start(-67, -62)
                        curveTo(
                            0, 30,
                            10*xMul, 20,
                            -16*xMul, 32,
                            reverseTangent
                        )
                        endVel(0.3)
                    }
                    andThen (
                        WaitUntilCommand(Robot::readyToShoot)
                        withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )
            )
        )

        val auto = (
            WaitCommand(0.01)
            andThen (
                ShootingStateOTM() racesWith (
                    (
                        followPath {
                            start(startPose.vector)
                            lineTo(-12 * xMul, 12, tangent)
                        }
                        andThen (
                            TankDrivetrain.headingLock(
                                PI/2 + PI/2 * xMul
                            )
                            until { abs(
                                TankDrivetrain.position.heading.toDouble()
                                - ( PI/2 + PI/2 * xMul)
                            ) < 0.1 && TankDrivetrain.velocity.heading < 0.5
                            }
                        )
                    )
                    parallelTo (
                        WaitUntilCommand(Robot::readyToShoot) withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )

            )
            andThen cycle1()
            andThen cycle2()
            andThen cycle3()
            andThen (
                If({grabPreloads}, cyclePreloads())
                Else cycleHP()
            )

        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}