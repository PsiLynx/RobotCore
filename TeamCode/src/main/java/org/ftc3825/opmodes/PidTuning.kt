package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.Telemetry
import kotlin.math.E
import kotlin.math.PI

@TeleOp(name="pid tuning")
class PidTuning: CommandOpMode() {
    override fun initialize() {
        Extendo.reset()

        val driver = Gamepad(gamepad1!!)
        driver.a.onTrue(OuttakeArm.runToPosition(0.0))
        driver.y.onTrue(OuttakeArm.runToPosition(PI / 2))

        Drivetrain.justUpdate().schedule()
        Telemetry.addAll {
            "pos" ids { OuttakeArm.angle }
            "setpoint" ids { OuttakeArm.leftMotor.setpoint }
            "effort" ids { OuttakeArm.leftMotor.lastWrite }
        }
        Telemetry.justUpdate().schedule()
    }
}