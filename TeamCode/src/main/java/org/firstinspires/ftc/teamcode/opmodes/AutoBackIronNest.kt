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
        var start = 0.0
        TankDrivetrain.position = startPose

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
                    /*
                    andThen followPath {
                        start(-52 * xMul, -12)
                        lineTo(-38 * xMul, -12, reverseTangent)
                    }.withConstraints(velConstraint = 5.0)
                     */
                )
            )
            /*
            andThen ( followPath {
                start(-38 * xMul, -12)
                curveTo(
                    -5 * xMul, 2.5,
                    -10 * xMul, 0,
                    -60 * xMul, -1,
                    tangent
                )
            } withTimeout(1) ) */
            andThen (
                ShootingStateOTM()
                racesWith (
                    /*
                    WaitCommand(0.3)
                    andThen */followPath {
                        //start(-60 * xMul, -1)
                        start(-52 * xMul, -12)
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
                    )
                )
            )
        )
        fun cycleTunnel(endHead: Double) = (
            (
                Intake.run()
                racesWith followPath {
                    start(-12 * xMul, 12)
                    curveTo(
                        -10*xMul, -10,
                        -20*xMul, 20,
                        -61, -12,
                        tangent
                    )
                }.withConstraints(velConstraint = 3.0)
            )
            andThen WaitUntilCommand { Globals.currentTime - start > 13 }
            andThen WaitCommand(2)
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    followPath {
                        start(-61 * xMul, -12)
                        curveTo(
                            20*xMul, -20,
                            10*xMul, 10,
                            -12*xMul, 12,
                            reverseTangent
                        )
                    }
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
                         followPath {
                             start(-52 * xMul, -36)
                             curveTo(
                                 20 * xMul, 0,
                                 20 * xMul, 20,
                                 -11 * xMul, 7,
                                reverseTangent
                            )
                        }
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


        val auto = (
            WaitCommand(0.01)
            andThen InstantCommand { start = Globals.currentTime }
            andThen (
                ShootingStateOTM() racesWith (
                    (
                        followPath {
                            start(startPose.vector)
                            lineTo(-12 * xMul, 12, reverseTangent)
                        }
                        andThen (
                            TankDrivetrain.headingLock(
                                -PI/2 - PI/4*xMul
                            )
                        )
                    )
                    racesWith (
                        //WaitCommand(0.5)
                        WaitUntilCommand(Robot::readyToShoot) withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )

            )
            andThen cycle2()
            andThen cycleTunnel(-PI/2 - PI/4*xMul)
            andThen cycleTunnel(3*PI/2)
            andThen cycle3()

        )

        (
            auto withTimeout 29.5
            andThen TankDrivetrain.power(0.7)
        ).schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}