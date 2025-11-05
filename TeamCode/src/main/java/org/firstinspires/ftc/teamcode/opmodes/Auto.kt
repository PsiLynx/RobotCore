package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingState
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

    override fun initialize() {

        Drivetrain.reset()
        Drivetrain.position = Pose2D(
            -51 * xMul, 51,
            PI / 2 + (degrees(50)*xMul)
        )

        Cameras.justUpdate().schedule()

        fun cycle(y: Double) = Intake.run() racesWith  (
            followPath {
                start(-20 * xMul, 20)
                lineTo(-20 * xMul, y, headingDir)
            }
            andThen followPath {
                start(-20 * xMul, y)
                lineTo(-53 * xMul, y, headingDir)
            }
            andThen followPath {
                start(-53 * xMul, y)
                lineTo(-20 * xMul, y, headingDir)

            }
            andThen (
                ShootingState({ Drivetrain.position.vector })
                racesWith  (
                    followPath {
                        start(-20 * xMul, y)
                        lineTo(
                            -20 * xMul, 20,
                            HeadingType.Constant(Rotation2D(
                                PI / 2 + (PI / 4 * xMul)
                            ))
                        )
                    }
                    andThen (
                        Robot.kickBalls()
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

        val cycle1 = cycle(12.0)
        val cycle2 = cycle(-12.0)
        val cycle3 = cycle(-36.0)


        val auto = (
            WaitCommand(0.1)
            andThen (
                ShootingState({ Drivetrain.position.vector })
                racesWith (
                    WaitCommand(1)
                    andThen (
                        Robot.kickBalls()
                        racesWith (
                            WaitCommand(2)
                            andThen Intake.run()
                        )
                    )
                )
            )
            andThen followPath {
                start(-51 * xMul, 51)
                lineTo(
                    -20 * xMul, 20,
                    headingDir
                )
            }
            andThen cycle1
            andThen cycle2
        )


        (
            ( auto withTimeout 29 )
            andThen followPath {
                start(-36 * xMul, 20)
                lineTo(-36 * xMul, 0, forward)
            }
        ).schedule()
    }
}