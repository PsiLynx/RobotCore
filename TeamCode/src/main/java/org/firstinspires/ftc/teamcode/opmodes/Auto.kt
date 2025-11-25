package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.AltShootingState
import org.firstinspires.ftc.teamcode.command.ShootingState
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.left
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.right
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
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
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.PI

@Autonomous
class Auto: CommandOpMode() {
    val startBack by SelectorInput("start in back", false, true)
    val pushPartner by SelectorInput("push partner", false, true)

    override fun preSelector() {
        Globals
        Cameras
        Drivetrain.pinpoint.reset()
        Drivetrain.ensurePinpointSetup()
    }
    override fun postSelector() {
        val xMul       = if (Globals.alliance == BLUE) 1    else -1
        val headingDir = if (Globals.alliance == BLUE) left else right

        Cameras.justUpdate().schedule()

        if(startBack){
            if(pushPartner) {
                Drivetrain.position = Pose2D(
                    -7*xMul, -64, PI/2 + PI/2*xMul
                )
            }
            else {
                Drivetrain.position = Pose2D(
                    -8*xMul, -65, PI/2
                )
            }
        }
        else {
            Drivetrain.position = if (Globals.alliance == BLUE) {
                Pose2D(
                    -51.5, 49.0,
                    2.402
                )
            } else Pose2D(
                49.5, 54.7, 0.514
            )
        }

        fun cycle(y: Double) = (
            followPath {
                start(-24 * xMul, 24)
                lineTo(-22 * xMul, y, headingDir)
            }.withConstraints(
                posConstraint = 5.0,
                velConstraint = 10.0
            )

            andThen (
                Intake.run()
                racesWith (
                    followPath {
                        start(-22 * xMul, y)
                        lineTo(-56 * xMul, y, headingDir)
                    }.withConstraints(velConstraint = 10.0)
                    withTimeout 1.3
                )
            )
            andThen (
                ShootingState({Drivetrain.position.vector})
                racesWith (
                    Drivetrain.power(0.0, 0.0, -1.0) withTimeout 0.2
                    andThen followPath {
                        start(-53 * xMul, y)
                        lineTo(
                            -24 * xMul, 24,
                            HeadingType.constant(
                                PI/2 + PI/4 * xMul
                            )
                        )
                    }.withConstraints(
                        posConstraint = 5.0,
                        velConstraint = 10.0,
                    )
                    andThen (
                        Robot.kickBalls()
                        racesWith Drivetrain.headingLock(
                            PI / 2 + PI / 4 * xMul
                        )
                    )
                )
            )
        )

        val cycle1 = cycle(8.0)
        val cycle2 = cycle(-16.0)
        //val cycle3 = cycle(-42.0)


        val auto = (
            WaitCommand(0.1)
            andThen Kicker.runToPos(0.3)
            andThen (
                ShootingState({Drivetrain.position.vector})
                racesWith (
                    If({startBack},
                        If({pushPartner},
                            Drivetrain.power(1.0, 0.0, 0.0) withTimeout 1
                        )
                        andThen followPath {
                            start(-7 * xMul, -65)
                            lineTo(-24 * xMul, 24, HeadingType.constant(
                                PI/2 + PI/4*xMul
                            ))
                        }.withConstraints(
                            posConstraint = 5.0,
                            velConstraint = 5.0,
                        )
                    ).Else (
                        followPath {
                            start(-50.5 * xMul, 49.5)
                            lineTo(
                                -24 * xMul, 24,
                                HeadingType.constant(
                                    PI/2 + PI/4*xMul
                                )
                            )
                        }.withConstraints(
                            posConstraint = 5.0,
                            velConstraint = 5.0,
                        )
                    )
                    andThen Robot.kickBalls()
                )
            )
            andThen cycle1
            andThen cycle2
            // Cycle 3
            andThen (
                followPath {
                    start(-24 * xMul, 24)
                    lineTo(-20 * xMul, -38, forward)
                }.withConstraints(velConstraint = 5.0)
                andThen ( Drivetrain.power(0.0, 0.0, 1.0) withTimeout 0.3 )
                andThen (
                    Intake.run()
                    racesWith (
                        followPath {
                            start(-20 * xMul, -38)
                            lineTo(-58 * xMul, -38, headingDir)
                        }.withConstraints(
                            posConstraint = 5.0,
                            velConstraint = 10.0,
                        )
                        withTimeout 1.5
                    )
                )
                andThen (
                    ShootingState({Drivetrain.position.vector})
                    racesWith (
                        Drivetrain.power(0.0, 0.0, -1.0) withTimeout 0.3
                        andThen followPath {
                            start(-53 * xMul, -38)
                            lineTo(
                                -24 * xMul, 24,
                                tangent()
                            )
                        }.withConstraints(
                            posConstraint = 5.0,
                            velConstraint = 10.0,
                        )
                        andThen (
                            ( WaitCommand(0.3) andThen Robot.kickBalls() )
                            racesWith Drivetrain.headingLock(
                                PI / 2 + PI / 4 * xMul
                            )
                        )
                    )
                )
            )
        )

        (
            ( auto withTimeout 29.0 )
            andThen (
                followPath {
                    start(-24 * xMul, 24)
                    lineTo(
                        -50 * xMul, 22,
                        tangent()
                    )
                }
                parallelTo Flywheel.setPower(-0.1)
            )
        ).schedule()
    }

}
