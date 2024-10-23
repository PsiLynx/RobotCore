package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Arm
import org.ftc3825.util.Pose2D
import org.ftc3825.command.internal.Trigger
import org.ftc3825.command.internal.CommandScheduler
import kotlin.math.abs

@Autonomous(name = "Drive Forward", group = "a")
class DriveForwardAuto: CommandOpMode() {

    override fun init() {
        initialize()

        Drivetrain.run {
            it.setWeightedDrivePower(Pose2D(
                  -0.25, 0.0, 0.0
            ))
        }.schedule()
    }
}
