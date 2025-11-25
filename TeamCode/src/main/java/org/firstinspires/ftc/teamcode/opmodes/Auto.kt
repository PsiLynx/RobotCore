package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.AltShootingState
import org.firstinspires.ftc.teamcode.command.ShootingState
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.left
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.right
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Cameras
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.GPP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PGP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PPG
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.PI

@Autonomous
class Auto: CommandOpMode() {
    val xMul get() = if (Globals.alliance == BLUE) 1 else -1

    val headingDir get() =
        if (Globals.alliance == BLUE) left
        else right

    override fun preSelector() {
        Drivetrain.reset()
        Cameras.justUpdate().schedule()
    }

    override fun postSelector() {
        Drivetrain.position = Pose2D(
            -50.5 * xMul, 49.5,
            PI / 2 + (degrees(43)*xMul)
        )

        fun cycle(y: Double) = Intake.run() racesWith  (
            followPath {
                start(-36 * xMul, 36)
                lineTo(-26 * xMul, y, headingDir)
            }.withConstraints(velConstraint = 5.0)

            andThen (
                ShootingStateOTM({ Drivetrain.position.vector }, { Drivetrain.velocity }) racesWith (

                    ( followPath {
                        start(-26 * xMul, y)
                        lineTo(-53 * xMul, y, headingDir)
                    }.withConstraints(velConstraint = 10.0) withTimeout 3 )
                    andThen followPath {
                        start(-53 * xMul, y)
                        lineTo(-45 * xMul, y, headingDir)
                        lineTo(
                            -36 * xMul, 36,
                            HeadingType.RelativeToTangent(Rotation2D(
                                PI/2 * xMul
                            ))
                        )
                    }.withConstraints(
                        posConstraint = 5.0,
                        velConstraint = 10.0,
                        headConstraint = PI/2
                    )

                    andThen (
                        ( WaitCommand(0.5) andThen Robot.kickBalls() )
                        racesWith Drivetrain.run {
                            it.headingController.targetPosition = (
                                PI/2 + PI/4*xMul
                            )

                            it.setWeightedDrivePower(
                                0.0, 0.0,
                                it.headingController.feedback,
                                0.03, true
                            )
                        } withEnd { Drivetrain.setWeightedDrivePower() }
                    )
                )
            )
        ) andThen Intake.stop()

        val cycle1 = cycle(10.0)
        val cycle2 = cycle(-14.0)
        val cycle3 = cycle(-38.0)


        val auto = (
            WaitCommand(0.1)
            andThen Kicker.runToPos(0.3)
            andThen (
                ShootingStateOTM({Drivetrain.position.vector},{Drivetrain.velocity})
                racesWith (
                    Intake.run() racesWith (
                        followPath {
                            start(-50.5 * xMul, 49.5)
                            lineTo(
                                -36 * xMul, 36,
                                HeadingType.constant(
                                    PI/2 + PI/4*xMul
                                )
                            )
                        }
                        andThen WaitCommand(0.3)
                        andThen Robot.kickBalls()
                    )
                )
            )
            andThen followPath {
                start(-50.5 * xMul, 49.5)
                lineTo(
                    -36 * xMul, 36,
                    headingDir
                )
            }
            andThen cycle1
            andThen cycle2
        )


        (
            ( auto withTimeout 29.3 )
            andThen (
                followPath {
                    start(-36 * xMul, 36)
                    lineTo(
                        -50 * xMul, 22,
                        HeadingType.tangent()
                    )
                }
                parallelTo Flywheel.setPower(-0.1)
            )
        ).schedule()
    }
}
