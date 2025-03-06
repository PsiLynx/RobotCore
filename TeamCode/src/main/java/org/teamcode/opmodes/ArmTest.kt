package org.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.teamcode.command.internal.RunCommand
import org.teamcode.component.Gamepad
import org.teamcode.subsystem.Drivetrain
import org.teamcode.subsystem.OuttakeArm
import org.teamcode.subsystem.Telemetry
import org.teamcode.util.Globals
import kotlin.math.PI

@TeleOp(name="arm test")
class ArmTest: CommandOpMode() {
    override fun initialize() {
        OuttakeArm.reset()

        val driver = Gamepad(gamepad1!!)
        driver.a.onTrue(OuttakeArm.runToPosition(0.0) until { false } )
        driver.y.onTrue(OuttakeArm.runToPosition(PI / 2) until { false } )

        val start = System.nanoTime()
        Drivetrain.justUpdate().schedule()
        OuttakeArm.justUpdate().schedule()
        Telemetry.addAll {
            "pos" ids { OuttakeArm.angle }
            "setpoint" ids { OuttakeArm.leftMotor.setpoint }
            "ticks" ids { OuttakeArm.leftMotor.ticks }
            "effort" ids { OuttakeArm.leftMotor.lastWrite }
            "p" ids { OuttakeArm.leftMotor.controllerParameters.P() }
            "voltage" ids { Globals.robotVoltage }
        }

        RunCommand {
            val time = (System.nanoTime() - start) / 1e9
            println("$time, ${OuttakeArm.angle}")
        }.schedule()
        Telemetry.justUpdate().schedule()
    }
}