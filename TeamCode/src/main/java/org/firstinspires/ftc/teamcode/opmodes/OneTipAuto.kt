package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.robot.Robot
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.PI

@Autonomous(name = "one tip")
class OneTipAuto : CommandOpMode() {
    override fun preSelector() {
        Drivetrain.io.position = (
                Pose2D(0, -72, PI) + Pose2D(-15.25, 15.25) / 2
                )
        Globals.alliance = Globals.Alliance.RED
    }
    override fun postSelector() {
        ShootingStateOTM().schedule()
        (
            WaitCommand(0.01)
            // shoot 4
            andThen Robot.kickBalls()
            // pickup 4
            andThen (
                followPath {
                    start(-(15.25 / 2), -(72 - (15.25 / 2)))
                    lineTo(-(72 - (15.25 / 2)), -(72 - (15.25 / 2)), HeadingType.tangent)
                } racesWith  Robot.intake()
            ) andThen (
                Intake.run() withTimeout 0.5
            )
            // shoot 4
            andThen followPath {
                start(-(72 - (15.25 / 2)), -(72 - (15.25 / 2)))
                lineTo((0 - (15.25 / 2)), -(72 - (15.25 / 2)), HeadingType.reverseTangent)
            }
            andThen Robot.kickBalls()
            // park
            andThen followPath {
                start((0 - (15.25 / 2)), -(72 - (15.25 / 2)))
                curveTo(
                    -20, 0,
                    0, 20,
                    -(70 - (15.25 / 2)),
                    24, HeadingType.tangent
                )
                endVel(1.0)
            }


        ).schedule()

    }
}