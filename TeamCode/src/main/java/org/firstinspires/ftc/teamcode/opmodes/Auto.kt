package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingState
import org.firstinspires.ftc.teamcode.command.internal.CommandGroup
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.Transfer
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.PI

@Autonomous
class AutoBlue: CommandOpMode() {
    val xMul get() = if (Globals.alliance == Globals.Alliance.BLUE) 1 else -1
    val headingDir get() = if (Globals.alliance == Globals.Alliance.BLUE){
        HeadingType.left
    } else HeadingType.right

    override fun initialize() {

        Globals.alliance = Globals.Alliance.BLUE
        Drivetrain.position = Pose2D(-50, 50, PI / 2 + degrees(50))


        fun intakeAndShoot(y: Double) = (
            (
                Intake.run()
                parallelTo followPath {
                    start(-24 * xMul, y)
                    lineTo(-53 * xMul, y * xMul, headingDir)
                }
            )
            andThen Intake.stop()
            andThen (
                ShootingState({ Drivetrain.position.vector })
                parallelTo (
                    followPath {
                        start(-53 * xMul, y)
                        lineTo(
                            -33 * xMul, 33,
                            HeadingType.Constant(Rotation2D(
                                PI / 2 + (
                                    degrees(45) * xMul
                                )
                            ))
                        )
                    }
                    andThen WaitUntilCommand(Robot::readyToShoot)
                    andThen Robot.kickBall()
                    andThen Robot.kickBall()
                )
            )
        )

        val auto = (
            WaitCommand(0.1)
            andThen (
                ShootingState({ Drivetrain.position.vector })
                parallelTo (
                        WaitUntilCommand(Robot::readyToShoot)
                        andThen Robot.kickBall()
                        andThen Robot.kickBall()
                )
            )
            andThen followPath {
                start(Drivetrain.position.vector)
                curveTo(
                    10 * xMul, -10,
                    0, -10,
                    -24 * xMul, 11.5,
                    headingDir
                )
            }
            andThen intakeAndShoot(11.5)
            andThen followPath {
                start(-33 * xMul, 33)
                lineTo(-20 * xMul, 0, headingDir)
                lineTo(-20 * xMul, 12.5, headingDir)
            }
            andThen intakeAndShoot(12.5)

        )

        (
            auto withTimeout 29
                andThen followPath {
                    start(-33 * xMul, 33)
                    lineTo(-33 * xMul, 0, HeadingType.forward)
            }
        )
    }
}