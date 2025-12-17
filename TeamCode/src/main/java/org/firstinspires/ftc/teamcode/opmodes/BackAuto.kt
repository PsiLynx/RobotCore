package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.left
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.right
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Flywheel.velocity
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.Telemetry.ids
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import org.firstinspires.ftc.teamcode.util.SelectorInput
import kotlin.math.PI

@Autonomous
class BackAuto: CommandOpMode() {


    override fun preSelector() {
        Globals
        Drivetrain.pinpoint.reset()
        Drivetrain.ensurePinpointSetup()
    }

    override fun postSelector() {
        val xMul = if (Globals.alliance == BLUE) 1 else -1
        val cycleOff = if (Globals.alliance == BLUE) 0 else 2
        val headingDir = if (Globals.alliance == BLUE) left else right

        val startPos = Pose2D(-17.071*xMul,-63.928,1.574)

        Drivetrain.position = startPos

        val shotPos = Pose2D(-14.718*xMul,16.546)

        fun cycle(y: Double) = (
                (
                        Intake.run()
                                racesWith (
                                followPath {
                                    lineTo(-20 * xMul, y, headingDir)
                                    lineTo(-56 * xMul, y, headingDir)
                                }.withConstraints(
                                    posConstraint = 5.0,
                                    velConstraint = 10.0
                                ) withTimeout 2
                                )
                        )
                        andThen (
                        ShootingStateOTM()
                                racesWith (
                                Drivetrain.power(0.0, 0.0, 1.0 * xMul) withTimeout 0.1
                                        andThen followPath {
                                    start(-53 * xMul, y)
                                    lineTo(
                                        shotPos.x, shotPos.y,
                                        HeadingType.constant(
                                            3 * PI/2 - PI/4 * xMul
                                        )
                                    )
                                }.withConstraints(
                                    posConstraint = 5.0,
                                    velConstraint = 10.0,
                                )
                                        andThen (
                                        (WaitCommand(0.5) andThen Robot.kickBalls())
                                                racesWith Drivetrain.headingLock(
                                            Drivetrain.shootingTargetHead
                                        )
                                        )
                                )
                        )
                )
        fun altCycle(y: Double) = (
                (Drivetrain.headingLock(PI/2) withTimeout 0.5)
                        andThen followPath {
                    start(-26 * xMul, 20)
                    lineTo(-24 * xMul, y + 3, HeadingType.reverseTangent)
                }
                        andThen (
                        Drivetrain.headingLock(
                            headingDir.theta.toDouble()
                        ) withTimeout 0.7
                        )
                        andThen (
                        Intake.run()
                                racesWith (
                                followPath {
                                    start(-24 * xMul, y)
                                    lineTo(-58 * xMul, y, headingDir)
                                }.withConstraints(
                                    posConstraint = 3.0,
                                    velConstraint = 10.0,
                                ) withTimeout 1.5
                                )
                        )
                        andThen (
                        ShootingStateOTM()
                                racesWith (
                                (
                                        Drivetrain.headingLock(
                                            Vector2D(
                                                -26 * xMul + 53 * xMul, 20 - y
                                            ).theta.toDouble()
                                                    + PI
                                        )
                                                withTimeout 0.5
                                        )
                                        andThen followPath {
                                    start(-53 * xMul, y)
                                    lineTo(
                                        shotPos.x,shotPos.y-2*xMul,
                                        HeadingType.reverseTangent
                                    )
                                }.withConstraints(
                                    posConstraint = 7.0,
                                    velConstraint = 10.0,
                                )
                                        andThen (
                                        (WaitCommand(0.5) andThen Robot.kickBalls())
                                                racesWith Drivetrain.headingLock(
                                            Drivetrain.shootingTargetHead
                                        )
                                        )
                                )
                        )
                )

        val cycle1 = cycle(11.0 + cycleOff)
        val cycle2 = altCycle(-15.0 + cycleOff)
        val cycle3 = altCycle(-39.0 + cycleOff)

        val auto = (
                WaitCommand(0.01)
                andThen (
                        ShootingStateOTM() racesWith (
                    followPath {
                        start(startPos.vector)
                        lineTo(-14.718*xMul,16.546,forward)
                    }
                            andThen (Drivetrain.headingLock(PI/2 + PI/4 * xMul) withTimeout 0.5)

                andThen Robot.kickBalls()
                        )
                        )
                andThen cycle2
                andThen cycle1
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
            "pos" ids Drivetrain::position
            "wheel vel" ids Flywheel::velocity
        }
    }

}