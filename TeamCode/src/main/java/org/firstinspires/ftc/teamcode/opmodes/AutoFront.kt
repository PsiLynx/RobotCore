package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.Cycle
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
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
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PPG
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PGP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.GPP
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
                -50.5, 53.0, -0.838 + 2*PI
            )
        } else Pose2D(
            50.3, 54.6, 3.8
        )
        TankDrivetrain.position = startPose

        val cycle1 = Cycle(
            startHeading = PI/2 + PI/2*xMul
        ) { nextStartHeading: Rotation2D -> (
            (
                Intake.run()
                racesWith followPath {
                    start(-12 * xMul, 12)
                    lineTo(-56 * xMul, 12, tangent)
                }.withConstraints(velConstraint = 3.0)
            )
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    followPath {
                        start(-56 * xMul, 12)
                        lineTo(
                            -12 * xMul, 12,
                            reverseTangent
                        )
                    }
                    andThen (
                        TankDrivetrain.headingLock(nextStartHeading)
                        withTimeout 0.5
                    )
                    andThen (
                        WaitUntilCommand(Robot::readyToShoot)
                        withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )
            )
        )}
        val cycle2 = Cycle(
            startHeading = -PI/2 - PI/4*xMul
        ) { nextStartHeading: Rotation2D -> (
            Intake.run()
            racesWith (
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
                andThen ( TankDrivetrain.power(-0.5) withTimeout 0.6 )
            )
            andThen ( followPath {
                start(-38 * xMul, -13)
                curveTo(
                    -5 * xMul, 2.5,
                    -10 * xMul, 0,
                    -58 * xMul, -2,
                    tangent
                )
            } withTimeout(1) )
            andThen (
                ShootingStateOTM()
                racesWith (
                    WaitCommand(1.0)
                    andThen followPath {
                        start(-60 * xMul, -6)
                        curveTo(
                            10 * xMul, -5,
                            10 * xMul, 20,
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
                    )
                    andThen (
                        TankDrivetrain.headingLock(nextStartHeading)
                        withTimeout 0.5
                    )
                )
            )
        )}
        val cycle3 = Cycle(
            startHeading = 3*PI/2
        ) { nextStartHeading: Rotation2D -> (
            Intake.run()
            racesWith followPath {
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
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    (
                        (
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
                        racesWith Intake.run(motorPow = 0.5)
                    )
                    andThen (
                        (
                            WaitUntilCommand(Robot::readyToShoot)
                            withTimeout 1
                            andThen Robot.kickBalls()
                        )
                    )
                    andThen (
                        TankDrivetrain.headingLock(nextStartHeading)
                        withTimeout 0.5
                    )
                )
            )
        )}
        val cyclePreloads = Cycle(
            startHeading = 3*PI/2
        ) { nextStartHeading: Rotation2D -> (
            Intake.run() racesWith followPath {
                start(-12 * xMul, 12)
                lineTo(-12 * xMul, -62, tangent)
            }
            andThen (
                ShootingStateOTM()
                racesWith (
                    (
                        followPath {
                            start(-12, -62)
                            lineTo(-12 * xMul, 28, reverseTangent)
                        } racesWith Intake.run(motorPow = 0.5)
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
        )}
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
                    (
                        TankDrivetrain.headingLock(cycle1.startHeading)
                        withTimeout 0.5
                    )
                    andThen cycle1(cycle2.startHeading)
                    andThen cycle2(cycle3.startHeading)
                    andThen cycle3(cyclePreloads.startHeading)
                )
                Else (
                    (
                        TankDrivetrain.headingLock(cycle3.startHeading)
                        withTimeout 0.5
                    )
                    andThen cycle3(cycle2.startHeading)
                    andThen cycle2(cycle1.startHeading)
                    andThen cycle1(cyclePreloads.startHeading)
                )
            )
            andThen cyclePreloads(3*PI/2)
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}