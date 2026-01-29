package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
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
    val startBack by SelectorInput("start in back", false, true)
    val pushPartner by SelectorInput("push partner", false, true)

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
                            /*WaitUntilCommand(Robot::readyToShoot)
                            withTimeout 1
                            andThen */Robot.kickBalls()
                        )
                        racesWith (
                            TankDrivetrain.power(0.0, 0.3) withTimeout 0.7
                            andThen TankDrivetrain.headingLock(3 * PI / 2)
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
                        lastTangent = Vector2D(0, -1)
                        arc(Arc.Direction.RIGHT * xMul, PI/2, 24, tangent)
                        endVel(0.5)
                        lineTo(-52 * xMul, -12, tangent)
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
                arcLineTo(
                    Arc.Direction.LEFT * xMul,
                    -60 * xMul, 0,
                    3,
                    tangent
                )
            } withTimeout(1) )
            andThen (
                ShootingStateOTM()
                racesWith (
                    followPath {
                        start(-53.5 * xMul, -3.5)
                        lastTangent = Vector2D(1 * xMul, -0.2)
                        lineTo(-12 * xMul, 12, reverseTangent)
                    }
                    andThen (
                        Robot.kickBalls()
                        racesWith TankDrivetrain.headingLock(3 * PI/2)
                    )
                )
            )
        )
        fun cycleTunnel(lastPose: Pose2D, straightDist: Double) = (
            (
                Intake.run()
                racesWith followPath {
                    start(-29 * xMul, 29)
                    lastTangent = Vector2D(1, 0) rotatedBy (
                        -PI/2 - 0.65*xMul
                    )
                    straight(45, tangent)
                    endVel(0.4)
                    arc(
                        Arc.Direction.LEFT * xMul,
                        0.65,
                        34,
                        tangent
                    )
                    endVel(0.4)
                    straight(straightDist, tangent)
                }.withConstraints(velConstraint = 3.0)
            )
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    followPath {
                        start(-67 * xMul, -26 - straightDist)
                        lastTangent = Vector2D(0, 1)
                        arcLineTo(
                            Arc.Direction.RIGHT * xMul,
                            lastPose.x, lastPose.y,
                            24,
                            reverseTangent
                        )
                    }
                    andThen (
                        Robot.kickBalls()
                        racesWith TankDrivetrain.headingLock(
                            lastPose.heading.toDouble()
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
                    lineTo(-12 * xMul, -16, tangent)
                    arc(Arc.Direction.RIGHT * xMul, PI/2, 20, tangent)
                    lineTo(-52 * xMul, -36, tangent)
                }.withConstraints(velConstraint = 3.0)
            )
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    (
                        TankDrivetrain.headingLock(
                            Vector2D(
                                -29 * xMul + 52 * xMul, 36 + 29
                            ).theta.toDouble() + PI
                        ) withTimeout 0.5
                    ) andThen followPath {
                        start(-52 * xMul, -36)
                        lineTo(
                            -29 * xMul, 29,
                            reverseTangent
                        )
                    }
                    andThen Robot.kickBalls()
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
                        WaitCommand(0.5)
                        //WaitUntilCommand(Robot::readyToShoot) withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )

            )
            andThen cycle1()
            andThen cycle2()
            andThen cycle3()
            andThen cycleTunnel(Pose2D(
                -29 * xMul, 29,
                PI/2 + (
                    PI/2 + degrees(49.268)
                ) * xMul
            ), 12.0)
            andThen cycleTunnel(Pose2D(-12 * xMul, 12, 3*PI/2), 17.9)
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}