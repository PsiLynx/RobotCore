package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
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
class AutoBackIronNest: CommandOpMode() {
    override fun preSelector() {
        Globals
        TankDrivetrain.resetLocalizer()
    }
    override fun postSelector() {
        val xMul  = if (Globals.alliance == BLUE) 1 else -1

        val startPose = Pose2D(
            -5 * xMul, -62, 3 * PI / 2
        )
        TankDrivetrain.position = startPose

        fun cycle2() = (
            (
                Intake.run()
                racesWith (
                    followPath {
                        start(-12 * xMul, 12)
                        curveTo(
                            0, -15,
                            -30*xMul, 0,
                            -58*xMul, -12,
                            tangent
                        )
                    }.withConstraints(
                        maxVel = 70.0,
                        aMax = 80.0,
                        velConstraint = 3.0,
                    )
                    //andThen ( TankDrivetrain.power(-0.5) withTimeout 0.5 )
                )
            )
            /*
            andThen ( followPath {
                start(-38 * xMul, -13)
                curveTo(
                    -5 * xMul, 2.5,
                    -10 * xMul, 2.5,
                    -60 * xMul, -2,
                    tangent
                )
            } withTimeout(1) )
            */
            andThen (
                ShootingStateOTM()
                racesWith (
                    WaitCommand(0.5)
                    andThen followPath {
                        //start(-60 * xMul, -3)
                        start(-58*xMul, -12)
                        curveTo(
                            10 * xMul, -5,
                            10 * xMul, 20,
                            -12 * xMul, 12,
                            reverseTangent
                        )
                    } andThen ( TankDrivetrain.headingLock(
                        3 * PI/2
                    ) withTimeout 0.5 )
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
        fun cycleTunnel(endHead: Double) = (
            Intake.run() racesWith (
                followPath {
                start(-12 * xMul, 12)
                curveTo(
                    -10*xMul, -10,
                    -20*xMul, 20,
                    -62.6, -24,
                    tangent
                    )
                }.withConstraints(velConstraint = 3.0)
                andThen WaitCommand(1.0)
            )
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    followPath {
                        start(-62.6 * xMul, -15)
                        curveTo(
                            20*xMul, -20,
                            10*xMul, 10,
                            -12*xMul, 12,
                            reverseTangent
                        )
                    }.withConstraints(aMax=80.0)
                    andThen (
                        Robot.kickBalls()
                        racesWith TankDrivetrain.headingLock(endHead)
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
                        -56 * xMul, -36,
                        tangent
                    )
                }.withConstraints(
                    aMax = 80.0,
                    dMax = 30.0,
                    maxVel = 70.0,
                    velConstraint = 3.0
                )
            )
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    (
                        (
                            TankDrivetrain.headingLock(-PI/2 - PI/6*xMul)
                            withTimeout 0.5
                        )
                        andThen followPath {
                             start(-56 * xMul, -36)
                             curveTo(
                                 10*xMul, 4,
                                 4*xMul, 10,
                                 -11 * xMul, 7,
                                reverseTangent
                            )
                        }.withConstraints(aMax = 120.0)
                        racesWith Intake.run(motorPow = 0.5)
                    )
                    andThen (
                        WaitUntilCommand(Robot::readyToShoot)
                        withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )
            )
        )

        fun cycleHP() = (
            Intake.run() racesWith followPath {
                start(-12 * xMul, 6)
                curveTo(
                    -15*xMul, -25,
                    0, -20,
                    -64*xMul, -60,
                    tangent
                )
            }
            andThen (
                ShootingStateOTM() racesWith (
                    followPath {
                        start(-64*xMul, -60)
                        curveTo(
                            0, 30,
                            20*xMul, 20,
                            -12*xMul, 12,
                            reverseTangent
                        )
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
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run(
                        motorPow = 0.5
                    ) )
                ) racesWith (
                    (
                        followPath {
                            start(startPose.vector)
                            lineTo(-12 * xMul, 6, reverseTangent)
                        }.withConstraints(aMax = 120.0)
                    )
                    andThen (
                        (
                            WaitUntilCommand(Robot::readyToShoot) withTimeout 1
                            andThen Robot.kickBalls()
                        )
                    )
                )

            )
            andThen cycle2()
            andThen cycle3()
            andThen cycleHP()
            andThen cycleHP()
            andThen (TankDrivetrain.power(0.7) withTimeout 1)

        )

        (auto).schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}