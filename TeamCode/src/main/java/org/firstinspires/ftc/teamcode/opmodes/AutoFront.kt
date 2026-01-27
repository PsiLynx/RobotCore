package org.firstinspires.ftc.teamcode.opmodes

import android.R.attr.y
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
        val cycleOff   = if (Globals.alliance == BLUE) 0    else  0

        val startPose = if (Globals.alliance == BLUE) {
            Pose2D(
                -51.5, 50.8, PI/2 + degrees(50) + PI
            )
        } else Pose2D(
            51.5, 50.8, PI/2 - degrees(50) + PI
        )
        TankDrivetrain.position = startPose

        fun cycle1(y: Double) = (
            (
                Intake.run()
                racesWith (
                    followPath {
                        start(-11 * xMul, 11)
                        lineTo(-56 * xMul, y, tangent)
                    }.withConstraints(
                        posConstraint = 5.0,
                        velConstraint = 10.0
                    ) withTimeout 2
                )
            )
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    followPath {
                        start(-56 * xMul, y)
                        lineTo(
                            -11 * xMul, y,
                            reverseTangent
                            )
                    }.withConstraints(
                        posConstraint = 5.0,
                        velConstraint = 10.0,
                    )
                    andThen (
                        (
                            WaitUntilCommand(Robot::readyToShoot)
                            withTimeout 1
                            andThen Robot.kickBalls()
                        )
                        racesWith TankDrivetrain.headingLock(3 * PI / 2)
                    )
                )
            )
        )
        fun cycle2(y: Double) = (
            (
                Intake.run()
                racesWith (
                    followPath {
                        start(-11 * xMul, 11)
                        lastTangent = Vector2D(0, -1)
                        arc(Arc.Direction.RIGHT, PI/2, 11 - y, tangent)
                        lineTo(-58 * xMul, y, tangent)
                    }.withConstraints(
                        posConstraint = 3.0,
                        velConstraint = 10.0,
                    )
                )
            )
            /*
            andThen followPath {
                start(-58 * xMul, y)
                lastTangent = Vector2D(1, 0)
                arc(Arc.Direction.LEFT, PI, 6, reverseTangent)
            }
             */
            andThen (
                (
                    ShootingStateOTM()
                    parallelTo ( Intake.run() withTimeout 0.5 )
                )
                racesWith (
                    followPath {
                        start(-58 * xMul, y/* - 12*/)
                        lineTo(-34 * xMul, y, reverseTangent)
                        arc(Arc.Direction.LEFT, PI/2, 11 - y, reverseTangent)
                    }.withConstraints(
                        posConstraint = 7.0,
                        velConstraint = 10.0,
                    )
                    andThen (
                        (
                            WaitUntilCommand(Robot::readyToShoot)
                            withTimeout 1
                            andThen Robot.kickBalls()
                        )
                        racesWith TankDrivetrain.headingLock(3 * PI / 2)
                    )
                )
            )
        )

        fun cycle3(y: Double) = (
            (
                Intake.run()
                racesWith (
                    followPath {
                        start(-20 * xMul, 20)
                        lineTo(-20, y + 23, tangent)
                        arc(Arc.Direction.RIGHT, PI/2, 23, tangent)
                        lineTo(-58 * xMul, y, tangent)
                    }.withConstraints(
                        posConstraint = 3.0,
                        velConstraint = 10.0,
                    )
                )
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
                                -18 * xMul + 58 * xMul, 36 - y
                            ).theta.toDouble() + PI
                        ) withTimeout 0.5
                    ) andThen followPath {
                        start(-58 * xMul, y)
                        lineTo(
                            -18 * xMul, 36,
                            reverseTangent
                        )
                    }.withConstraints(
                        posConstraint = 7.0,
                        velConstraint = 10.0,
                    )
                    andThen Robot.kickBalls()
                )
            )
        )

        val cycle1 = cycle1(12.0 + cycleOff)
        val cycle2 = cycle2(-12.0 + cycleOff)
        val cycle3 = cycle3(-36.0 + cycleOff)


        val auto = (
            WaitCommand(0.01)
            andThen (
                ShootingStateOTM() racesWith (
                    (
                        followPath {
                            start(startPose.vector)
                            lineTo(-11 * xMul, 11, tangent)
                        }.withConstraints(
                            posConstraint = 5.0,
                            aMax = 30.0,
                            dMax = 30.0,
                        )
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
            andThen cycle1
            andThen cycle2
            andThen cycle3
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}