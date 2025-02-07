package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.Telemetry
import kotlin.math.PI

@TeleOp(name="pid tuning")
class PidTuning: CommandOpMode() {
    override fun init() {
        initialize()
        OuttakeArm.reset()
        val driver = Gamepad(gamepad1!!)

        OuttakeArm.run {
            it.setPower(driver.leftStickY.toDouble())
        }.schedule()

        driver.x.onTrue( OuttakeArm.runToPosition(0.0) )
        driver.b.onTrue( OuttakeArm.runToPosition(PI / 2) )
        driver.a.onTrue( OuttakeArm.runOnce { it.angle = 0.0 } )
        Telemetry.addAll {
            "pos" ids { OuttakeArm.angle }
            "ticks" ids { OuttakeArm.leftMotor.ticks }
            "setpoint" ids { OuttakeArm.leftMotor.setpoint }
            "effort" ids { OuttakeArm.leftMotor.lastWrite }
        }
        Telemetry.justUpdate().schedule()
    }
}