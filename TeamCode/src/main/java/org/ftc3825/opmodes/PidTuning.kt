package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.geometry.Vector2D
import kotlin.random.Random

@TeleOp(name="pid tuning")
class PidTuning: CommandOpMode() {
    override fun init() {
        initialize()
        Extendo.reset()
        val driver = Gamepad(gamepad1!!)

        Extendo.run {
            it.setPower(
                Vector2D(
                    driver.leftStickX.toDouble(),
                    driver.leftStickY.toDouble(),
                )
            )
        }.schedule()
        Telemetry.addAll { 
            "x" ids Extendo.position::x
            "y" ids Extendo.position::y
            "test" ids driver::leftStickY
        }
        Telemetry.justUpdate().schedule()
    }
}