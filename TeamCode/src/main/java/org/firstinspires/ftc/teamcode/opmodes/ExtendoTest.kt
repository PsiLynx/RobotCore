package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Extendo
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals

@TeleOp(name="extendo test")
class ExtendoTest: CommandOpMode() {
    override fun initialize() {
        Extendo.reset()

        val driver = Gamepad(gamepad1!!)

        driver.apply {
            a.onTrue(Extendo.setY(0.01) until { false })
            b.onTrue( InstantCommand { Extendo.reset() } )
            y.onTrue(Extendo.setY(0.1) until { false })
        }

        Drivetrain.justUpdate().schedule()
        Extendo.justUpdate().schedule()
        RunCommand { Thread.sleep(10L) }.schedule()
        Telemetry.addAll {
            "setpoint" ids { Extendo.targetPos }
            "ticks" ids { Extendo.leftMotor.ticks }
            "effort" ids { Extendo.leftMotor.lastWrite }
            "voltage" ids { Globals.robotVoltage }
        }

        Telemetry.justUpdate().schedule()
    }
}
