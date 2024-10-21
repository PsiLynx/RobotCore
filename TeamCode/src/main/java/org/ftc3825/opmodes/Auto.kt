package org.ftc3825.opmodes

import org.ftc3825.command.internal.CommandScheduler
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Path
import org.ftc3825.GVF.Spline
import org.ftc3825.command.DriveCommand
import org.ftc3825.command.DriveCommand.Direction.FORWARD
import org.ftc3825.command.DriveCommand.Direction.BACK
import org.ftc3825.command.DriveCommand.Direction.RIGHT
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Intake
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.util.Pose2D
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.command.internal.TimedCommand
import org.ftc3825.command.internal.InstantCommand

const val width = 12.0
const val height = 14.0

@Autonomous(name = "Auto", group = "a")
class Auto: CommandOpMode() {
    override fun init() {
        initialize()

        var driveForward = (
            Drivetrain.run {
                it.setWeightedDrivePower(Pose2D(0.5, 0, 0))
            } until { Drivetrain.encoders[0].distance > 2000 }
        )

        var moveArmUp = (
            //OuttakeSlides.moveToBar()
            InstantCommand {
                Arm.pitchUp()
                Claw.pitchDown()
            }
        )

        var hangSample = /*OuttakeSlides.moveBelowBar() andThen */Claw.release()

        var retract = (
            OuttakeSlides.retract()
            //parallelTo Arm.pitchUp()
            //parallelTo Claw.pitchUp()
        )

        var park = (
            TimedCommand(0.5) { Drivetrain.setWeightedDrivePower(-0.5, 0.0, 0.0) }
            andThen TimedCommand(2) {
                Drivetrain.setWeightedDrivePower(Pose2D(0.0, 0.5, 0.0))
            }
            andThen TimedCommand(2) {
                Drivetrain.setWeightedDrivePower(Pose2D(-0.5, 0.0, 0.0))
            }
        )

    }
}
