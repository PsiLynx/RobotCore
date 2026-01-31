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
                        lastTangent = Vector2D(0, -1)
                        arc(Arc.Direction.RIGHT * xMul, PI/2, 24, tangent)
                        endVel(0.5)
                        lineTo(-52 * xMul, -12, tangent)
                    }.withConstraints(velConstraint = 3.0)
                    /*
                    andThen followPath {
                        start(-52 * xMul, -12)
                        lineTo(-38 * xMul, -12, reverseTangent)
                    }.withConstraints(
                        velConstraint = 5.0
                    )
                     */
                )
            )
            /*
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
             */
            andThen (
                ShootingStateOTM()
                racesWith (
                    /*
                    followPath {
                        start(-53.5 * xMul, -3.5)
                        lastTangent = Vector2D(1 * xMul, -0.3)
                        arcLineTo(
                            Arc.Direction.LEFT,
                            -12 * xMul, 12,
                            36,
                            reverseTangent
                        )
                    }
                     */
                    followPath {
                        start(-52 * xMul, -12)
                        lineTo(-36, -12, reverseTangent)
                        endVel(0.5)
                        arc(
                            Arc.Direction.LEFT * xMul,
                            PI/2,
                            24,
                            reverseTangent
                        )
                    }
                    andThen (
                        Robot.kickBalls()
                        racesWith TankDrivetrain.headingLock(
                            -PI/2 - PI/4*xMul
                        )
                    )
                )
            )
        )
        fun cycleTunnel(endHead: Double) = (
            (
                Intake.run()
                racesWith followPath {
                    start(-12 * xMul, 12)
                    lastTangent = Vector2D(1, 0) rotatedBy (
                        -PI/2 - PI/4*xMul
                    )
                    straight(32, tangent)
                    endVel(0.3)
                    arc(
                        Arc.Direction.RIGHT * xMul,
                        PI/2,
                        18,
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
                    followPath {
                        start(-60 * xMul, -12)
                        lastTangent = Vector2D(1 * xMul, -1)
                        arc(
                            Arc.Direction.LEFT * xMul,
                            PI/2,
                            18,
                            reverseTangent
                        )
                        lineTo(-12 * xMul, 12, reverseTangent)
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
                    followPath {
                        start(-52 * xMul, -36)
                        lineTo(-32 * xMul, -36, reverseTangent)
                        arc(Arc.Direction.LEFT * xMul, PI/2, 20, reverseTangent)
                        lineTo(-12 * xMul, 3, reverseTangent)
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
                            lineTo(-12 * xMul, 12, reverseTangent)
                        }
                    )
                    parallelTo (
                        WaitCommand(0.5)
                        //WaitUntilCommand(Robot::readyToShoot) withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )

            )
            andThen cycle2()
            andThen cycleTunnel(-PI/2 - PI/4*xMul)
            andThen cycleTunnel(3*PI/2)
            andThen cycle3()
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}