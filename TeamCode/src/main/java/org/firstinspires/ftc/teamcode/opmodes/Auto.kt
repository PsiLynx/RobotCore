package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingState
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
class Auto: CommandOpMode() {
    val xMul get() = if (Globals.alliance == Globals.Alliance.BLUE) 1 else -1

    override fun initialize() {

        Drivetrain.position = Pose2D(
            -50 * xMul, 50,
            PI / 2 + (degrees(50)*xMul)
        )

        val auto = (
            WaitCommand(0.1)
            andThen WaitCommand(0.1)
            andThen (
                Drivetrain.run {
                    it.setWeightedDrivePower(-0.5)
                } withTimeout 0.5
                withEnd {
                    Drivetrain.setWeightedDrivePower()
                }
            )
            andThen (
                Flywheel.shootingState {
                    ( Globals.goalPose.groundPlane -
                            Drivetrain.position.vector
                            ).mag
                }
                racesWith (
                    WaitCommand(2)
                    andThen (
                        (Transfer.run() parallelTo Intake.run())
                        withTimeout 7
                    )
                )
            )
            andThen followPath {
                start(-50 * xMul, 50)
                lineTo(
                    -36 * xMul, 0,
                    HeadingType.forward
                )
            }

        )

        (
            auto
        ).schedule()
    }
}