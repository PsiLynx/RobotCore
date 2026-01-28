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
                -51.5, 50.8, PI/2 + degrees(50) + PI
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
                    lineTo(-56 * xMul, 12, tangent)
                }
            )
            andThen (
                ShootingStateOTM()
                racesWith (
                    followPath {
                        start(-56 * xMul, 12)
                        lineTo(
                            -12 * xMul, 12,
                            reverseTangent
                        )
                    }
                    andThen (
                        Robot.kickBalls()
                        racesWith TankDrivetrain.headingLock(3 * PI / 2)
                    )
                )
            )
        )
        fun cycle2() = (
            (
                Intake.run()
                racesWith followPath {
                    start(-12 * xMul, 12)
                    lineTo(-12 * xMul, 8, tangent)
                    arc(Arc.Direction.RIGHT * xMul, PI/2, 20, tangent)
                    lineTo(-58 * xMul, -12, tangent)
                }
            )
            andThen followPath {
                start(-58 * xMul, -12)
                lastTangent = Vector2D(1 * xMul, 0)
                arc(Arc.Direction.LEFT * xMul, PI, 6, reverseTangent)
            }
            andThen (
                ShootingStateOTM()
                racesWith (
                    followPath {
                        start(-58 * xMul, 0)
                        lastTangent = Vector2D(1 * xMul, 0)
                        arcLineTo(
                            Arc.Direction.LEFT * xMul,
                            -29 * xMul, 29,
                            12,
                            tangent
                        )
                    }
                    andThen (
                        Robot.kickBalls()
                        racesWith TankDrivetrain.headingLock(
                            PI/2 + (
                                PI/2 + degrees(49.268)
                            ) * xMul
                        )
                    )
                )
            )
        )
        fun cycleTunnel(lastPose: Pose2D, straightDist: Double) = (
            (
                Intake.run()
                racesWith followPath {
                    start(-29 * xMul, 29)
                    lineTo(-60 * xMul, -7, tangent)
                    arc(
                        Arc.Direction.LEFT * xMul,
                        degrees(40.732),
                        28.898,
                        tangent
                    )
                    straight(straightDist, tangent)
                }
            )
            andThen (
                ShootingStateOTM()
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
                    lineTo(-58 * xMul, -36, tangent)
                }
            )
            andThen (
                ShootingStateOTM()
                racesWith (
                    (
                        TankDrivetrain.headingLock(
                            Vector2D(
                                -18 * xMul + 58 * xMul, 36 +36
                            ).theta.toDouble() + PI
                        ) withTimeout 0.5
                    ) andThen followPath {
                        start(-58 * xMul, -36)
                        lineTo(
                            -18 * xMul, 36,
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
                        }.withConstraints(
                            posConstraint = 5.0,

                        )
                        andThen (
                            TankDrivetrain.headingLock(
                                PI/2 + PI/2 * xMul
                            )
                            until { abs(
                                TankDrivetrain.position.heading.toDouble()
                                - ( PI/2 + PI/2 * xMul)
                            ) < 0.1 }
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
            andThen cycleTunnel(Pose2D(
                -29 * xMul, 29,
                PI/2 + (
                    PI/2 + degrees(49.268)
                ) * xMul
            ), 8.0)
            andThen cycleTunnel(Pose2D(-12 * xMul, 12, 3*PI/2), 17.4)
            andThen cycle3()
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}