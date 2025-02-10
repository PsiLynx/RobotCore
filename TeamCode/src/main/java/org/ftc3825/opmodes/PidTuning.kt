package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.ClipIntake
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.Telemetry
import kotlin.math.E
import kotlin.math.PI

@TeleOp(name="pid tuning")
class PidTuning: CommandOpMode() {
    override fun init() {
        initialize()
        Extendo.reset()

        val driver = Gamepad(gamepad1!!)
        driver.a.onTrue(Extendo.setY(0.1))
        driver.y.onTrue(Extendo.setY(1.0))
        Telemetry.addAll {
            "pos" ids { OuttakeArm.angle }
            "ticks" ids { OuttakeArm.leftMotor.ticks }
            "setpoint" ids { OuttakeArm.leftMotor.setpoint }
            "effort" ids { OuttakeArm.leftMotor.lastWrite }
        }
        Telemetry.justUpdate().schedule()
    }
}