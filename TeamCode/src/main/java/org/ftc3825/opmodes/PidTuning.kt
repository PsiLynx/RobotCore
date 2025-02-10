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
    override fun init() {
        initialize()
        Extendo.reset()

        val driver = Gamepad(gamepad1!!)
        driver.a.onTrue(Extendo.setX(0.1))
        driver.y.onTrue(Extendo.setX(0.4))

        Drivetrain.justUpdate().schedule()
        Telemetry.addAll {
            "pos" ids { Extendo.position.x }
            "setpoint" ids { Extendo.xAxisServo.setpoint }
            "effort" ids { Extendo.xAxisServo.lastWrite }
        }
        Telemetry.justUpdate().schedule()
    }
}