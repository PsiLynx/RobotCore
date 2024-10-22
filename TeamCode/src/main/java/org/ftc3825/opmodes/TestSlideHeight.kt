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
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.subsystem.Telemetry

@Autonomous(name = "TestSlideHeight", group = "a")
class TestSlideHeight: CommandOpMode() {
    override fun init() {
        initialize()
        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("") { Drivetrain.encoders[0].distance.toString() }
        Telemetry.justUpdate().schedule()

        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.grab()
        }.schedule()
        var moveSlidesALittle = OuttakeSlides.runToPosition(480.0)

        var moveArmUp = (
                RunCommand(OuttakeSlides) { OuttakeSlides.setPower(0.5) } until { OuttakeSlides.position > 910} withEnd { OuttakeSlides.setPower(0.0)}
                        andThen WaitCommand(1)
                )

        ( moveSlidesALittle andThen WaitCommand(1) andThen moveArmUp
                ).schedule()
    }
}
