package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.AnalogEncoder
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.flMotorName
import kotlin.math.PI

@TeleOp(name="arm test")
class ArmTest: CommandOpMode() {
    override fun initialize() {
        OuttakeArm.reset()
        val encoder = GlobalHardwareMap.get(
            AnalogInput::class.java, "outtake arm"
        )

        val driver = Gamepad(gamepad1!!)

        driver.apply {
            a.onTrue(OuttakeArm.runToPosition(0.0) until { false })
            b.onTrue( InstantCommand { OuttakeArm.reset() } )
            y.onTrue(OuttakeArm.runToPosition(PI / 2) until { false })
        }

        val start = System.nanoTime()
        Drivetrain.justUpdate().schedule()
        OuttakeArm.justUpdate().schedule()
        RunCommand { Thread.sleep(10L) }.schedule()
        Telemetry.addAll {
            "setpoint" ids { OuttakeArm.leftMotor.setpoint }
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
