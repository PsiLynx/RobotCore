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
                    -50.5, 49.5,
                    PI / 2 + degrees(43)
                )
            } else Pose2D(
                49.5, 54.7, 0.514
            )
        }

        fun cycle(y: Double) = Intake.run() racesWith  (
            followPath {
                start(-36 * xMul, 36)
                lineTo(-26 * xMul, y, headingDir)
            }.withConstraints(velConstraint = 5.0)

            andThen (
                ShootingState({Drivetrain.position.vector}) racesWith (

                    ( followPath {
                        start(-26 * xMul, y)
                        lineTo(-56 * xMul, y, headingDir)
                    }.withConstraints(velConstraint = 10.0) withTimeout 3 )
                    andThen followPath {
                        start(-53 * xMul, y)
                        lineTo(-45 * xMul, y, headingDir)
                        lineTo(
                            -36 * xMul, 36,
                            HeadingType.linear(
                                0.0, PI/4 * xMul
                            )
                        )
                    }.withConstraints(
                        posConstraint = 5.0,
                        velConstraint = 10.0,
                        headConstraint = PI/2
                    )

                    andThen (
                        ( WaitCommand(0.5) andThen Robot.kickBalls() )
                        racesWith Drivetrain.headingLock(
                            PI / 2 + PI / 4 * xMul
                        )
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
                ShootingState({Drivetrain.position.vector})
                racesWith (
                    If({startBack},
                        If({pushPartner},
                            Drivetrain.power(1.0, 0.0, 0.0) withTimeout 1
                        )
                        andThen followPath {
                            start(-7 * xMul, -65)
                            lineTo(-36 * xMul, 36, tangent())
                        }
                    ).Else (
                        followPath {
                            start(-50.5 * xMul, 49.5)
                            lineTo(
                                -36 * xMul, 36,
                                HeadingType.constant(
                                    PI/2 + PI/4*xMul
                                )
                            )
                        }
                    )
                )
                andThen (
                    Drivetrain.headingLock(
                        PI / 2 + PI / 4 * xMul
                    )
                    racesWith (
                        WaitCommand(0.3)
                        andThen Robot.kickBalls()
                    )
                )
            )
            andThen cycle1
            andThen cycle2
        )


        (
            ( auto withTimeout 29.0 )
            andThen (
                followPath {
                    start(-36 * xMul, 36)
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
