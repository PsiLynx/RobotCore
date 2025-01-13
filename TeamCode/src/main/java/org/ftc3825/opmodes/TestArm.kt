package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Pose2D

@TeleOp(name = "test arm", group = "a")
class TestArm: CommandOpMode() {
    override fun init() {
        initialize()
        Arm.reset()
        Claw.reset()
        Drivetrain.reset()
        Drivetrain.position = Pose2D(8.0, 66.0, 0.0)
        Drivetrain.update()

        Drivetrain.justUpdate().schedule()
        Claw.wallPitch().schedule()
        Claw.justUpdate().schedule()
        Arm.wallPitch().schedule()

        Telemetry.addFunction("pos") { Drivetrain.position }
        Telemetry.justUpdate().schedule()
        Telemetry.update()

    }
}