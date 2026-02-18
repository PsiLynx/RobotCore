package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.intake
import org.firstinspires.ftc.teamcode.command.shoot
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import kotlin.math.PI

@Autonomous
class Auto6LightBotics: CommandOpMode() {
    override fun preSelector() {
        Globals
        TankDrivetrain.resetLocalizer()
    }

    override fun postSelector() {
        val xMul       = if (Globals.alliance == BLUE) 1    else -1
        val startPose = (
            if (Globals.alliance == BLUE) {
                Pose2D(-50.8, 53.4, -0.838 + 2 * PI)
            } else Pose2D(51.25, 53.8, 3.8)
        )
        TankDrivetrain.position = startPose

        val cycle1 =  (
            intake(
                 followPath {
                     start(-45 * xMul, 45)
                     lineTo(-55 * xMul, 30, tangent)
                     curveTo(
                         0, -5,
                         30 * xMul, 10,
                         -35 * xMul, 12,
                         tangent
                     )
                }.withConstraints(
                    aMax = 70.0, dMax = 30.0, maxVel = 20.0
                )
            )
            andThen shoot(
                followPath {
                    start(-35 * xMul, 12)
                    curveTo(
                        -30 * xMul, 0,
                        -10 * xMul, 10,
                        -45 * xMul, 45,
                        reverseTangent
                    )
                }.withConstraints(
                    aMax = 70.0, dMax = 30.0, maxVel = 20.0
                )
            )
        )
        val auto = (
            WaitCommand(0.01)
            andThen (
                (
                    WaitCommand(0.3)
                    andThen ShootingStateOTM()
                ) racesWith (
                    (
                        followPath {
                            start(startPose.vector)
                            lineTo(-45 * xMul, 45, tangent)
                        }
                    )
                    andThen (
                        WaitUntilCommand(Robot::readyToShoot) withTimeout 1
                        andThen Robot.kickBalls()
                    )
                )

            )
            andThen cycle1
        )

        auto.schedule()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "wheel vel" ids Flywheel::currentState
        }
    }

}