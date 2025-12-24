package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingState
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.gvf.Arc
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.left
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.right
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.GPP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PGP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PPG
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.PI
import kotlin.math.sqrt

@Autonomous
class Auto: CommandOpMode() {
    val startBack by SelectorInput("start in back", false, true)
    val pushPartner by SelectorInput("push partner", false, true)

    override fun preSelector() {
        Globals
        TankDrivetrain.resetLocalizer()
    }
    override fun postSelector() {
        val xMul       = if (Globals.alliance == BLUE) 1    else -1
        val cycleOff   = if (Globals.alliance == BLUE) 0    else  2
        val headingDir = if (Globals.alliance == BLUE) left else right

        if(startBack){
            if(pushPartner) {
                TankDrivetrain.position = Pose2D(
                    -7*xMul, -64, PI/2 - PI/2*xMul
                )
            }
            else {
                TankDrivetrain.position = Pose2D(
                    -8*xMul, -65, PI/2
                )
            }
        }
        else {
            TankDrivetrain.position = if (Globals.alliance == BLUE) {
                Pose2D(
                    -51.65, 50.37,
                    2.379
                )
            } else Pose2D(
                48.0, 60.87, 0.538
            )
        }

        fun cycle1(y: Double) = (
            /*( TankDrivetrain.headingLock(
                PI/2 + PI/2 * xMul
            ) withTimeout 0.3 )
            andThen */(
                Intake.run()
                racesWith (
                    followPath {
                        lineTo(-20 * xMul, y, HeadingType.tangent)
                        lineTo(-56 * xMul, y, HeadingType.tangent)
                    }.withConstraints(
                        posConstraint = 5.0,
                        velConstraint = 10.0
                    ) withTimeout 2
                )
            )
            andThen (
                ShootingStateOTM()
                racesWith (
                    TankDrivetrain.power(0.0, 1.0 * xMul) withTimeout 0.1
                    andThen followPath {
                        start(-53 * xMul, y)
                        lineTo(
                            -26 * xMul, 20,
                            HeadingType.reverseTangent
                            )
                    }.withConstraints(
                        posConstraint = 5.0,
                        velConstraint = 10.0,
                    )
                    andThen (
                        (WaitCommand(0.3) andThen Robot.kickBalls())
                        /*
                        racesWith TankDrivetrain.headingLock(
                            TankDrivetrain.shootingTargetHead
                        )
                         */
                    )
                )
            )
        )
        fun cycle2(y: Double) = (
            /*(TankDrivetrain.headingLock(PI/2) withTimeout 0.5)
            andThen */followPath {
                start(-26 * xMul, 20)
                lineTo(-24 * xMul, y + 3, HeadingType.reverseTangent)
            }
            /*
            andThen (
                TankDrivetrain.headingLock(
                    headingDir.theta.toDouble()
                ) withTimeout 0.7
            )
             */
            andThen (
                Intake.run()
                racesWith (
                    followPath {
                        start(-24 * xMul, y)
                        lineTo(-58 * xMul, y, HeadingType.tangent)
                    }.withConstraints(
                        posConstraint = 3.0,
                        velConstraint = 10.0,
                    )
                )
            )
            andThen followPath {
                start(-58*xMul, y)
                arcLeft(PI,8, HeadingType.reverseTangent)
            }
            andThen (
                ShootingStateOTM()
                racesWith (
                    /*(
                        TankDrivetrain.headingLock(
                            Vector2D(
                            -26 * xMul + 53 * xMul, 20 - y
                            ).theta.toDouble()
                            + PI
                        )
                        withTimeout 0.5
                    )
                    andThen */ followPath {
                        start(-53 * xMul, y)
                        lineTo(
                            -26 * xMul, 20,
                            HeadingType.reverseTangent
                        )
                    }.withConstraints(
                        posConstraint = 7.0,
                        velConstraint = 10.0,
                    )
                    andThen (
                        (WaitCommand(0.3) andThen Robot.kickBalls())
                        /*
                        racesWith TankDrivetrain.headingLock(
                            TankDrivetrain.shootingTargetHead
                        )
                         */
                    )
                )
            )
        )
        fun cycle3(y: Double) = (
            /*(TankDrivetrain.headingLock(PI/2) withTimeout 0.5)
            andThen*/ followPath {
                start(-26 * xMul, 20)
                lineTo(-24 * xMul, y + 3, HeadingType.reverseTangent)
            }
            /*andThen (
                TankDrivetrain.headingLock(
                    headingDir.theta.toDouble()
                ) withTimeout 0.7
            )*/
            andThen (
                Intake.run()
                racesWith (
                    followPath {
                        start(-24 * xMul, y)
                        lineTo(-58 * xMul, y, HeadingType.tangent)
                    }.withConstraints(
                        posConstraint = 3.0,
                        velConstraint = 10.0,
                    ) withTimeout 1.5
                )
            )
            andThen (
                ShootingStateOTM()
                racesWith (
                    /*(
                        TankDrivetrain.headingLock(
                            Vector2D(
                                -26 * xMul + 53 * xMul, 20 - y
                            ).theta.toDouble() + PI
                        ) withTimeout 0.5
                    ) andThen*/ followPath {
                        start(-53 * xMul, y)
                        lineTo(
                            -26 * xMul, 20,
                            HeadingType.reverseTangent
                        )
                    }.withConstraints(
                        posConstraint = 7.0,
                        velConstraint = 10.0,
                    )
                    andThen (
                        (WaitCommand(0.3) andThen Robot.kickBalls())
                            /*
                            racesWith TankDrivetrain.headingLock(
                                TankDrivetrain.shootingTargetHead
                            )
                             */
                    )
                )
            )
        )

        val cycle1 = cycle1(11.0 + cycleOff)
        val cycle2 = cycle2(-13.0 + cycleOff)
        val cycle3 = cycle3(-37.0 + cycleOff)


        val auto = (
            WaitCommand(0.01)
            andThen (
                If({startBack},
                    ShootingStateOTM()
                        racesWith (
                        If({pushPartner},
                            TankDrivetrain.power(-1.0, 0.0) withTimeout 1
                        )
                        andThen followPath {
                            start(-7 * xMul, -65)
                            lineTo(-20 * xMul, 20, HeadingType.tangent
                            )
                        }.withConstraints(
                            posConstraint = 5.0,
                            velConstraint = 5.0,
                        )
                        andThen Robot.kickBalls()
                    )
                ).Else (
                    ShootingStateOTM() racesWith (
                        (
                            TankDrivetrain.power(-0.3, 0.0)
                            until {(
                                TankDrivetrain.position.vector.mag
                                < sqrt(2.0) * 32
                            )}
                            andThen RunCommand { }
                        )
                        racesWith  (
                            WaitCommand(0.7)
                            andThen Robot.kickBalls()
                        )
                    )
                    andThen followPath {
                        start(-50.5 * xMul, 49.5)
                        lineTo(
                            -20 * xMul, 11 + cycleOff,
                            HeadingType.reverseTangent
                        )
                    }.withConstraints(
                        posConstraint = 5.0,
                        velConstraint = 5.0,
                    )
                )
            )
            andThen cycle1
            andThen cycle2
            andThen cycle3
        )

        (
            ( auto withTimeout 29.3 )
            andThen (
                followPath {
                    start(-26 * xMul, 20)
                    lineTo(
                        -50 * xMul, -7,
                        HeadingType.constant(PI/2 + PI/4 * xMul)
                    )
                }
                parallelTo Flywheel.setPower(-0.1)
            )
        ).schedule()
        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}