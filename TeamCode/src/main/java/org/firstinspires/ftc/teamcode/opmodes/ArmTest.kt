package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.AnalogInput
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.PI

@TeleOp(name="arm test")
class ArmTest: CommandOpMode() {
    override fun initialize() {
        OuttakeArm.reset()
        val encoder = HardwareMap.get(
            AnalogInput::class.java, "outtake arm"
        )

        val driver = Gamepad(gamepad1!!)

        driver.apply {
            a.onTrue(OuttakeArm.runToPosition(0.0) until { false })
            b.onTrue( InstantCommand { OuttakeArm.reset() } )
            y.onTrue(OuttakeArm.runToPosition(PI / 2) until { false })
            dpadLeft.onTrue(OuttakeClaw.rollUp()) // outtake
            dpadRight.onTrue(OuttakeClaw.rollDown()) // intake
            dpadUp.onTrue(OuttakeClaw.toggleGrip())
        }

        val start = System.nanoTime()
        Drivetrain.justUpdate().schedule()
        OuttakeArm.justUpdate().schedule()
        RunCommand { Thread.sleep(10L) }.schedule()
        Telemetry.addAll {
            "pos" ids { OuttakeArm.angle }
            "setpoint" ids { OuttakeArm.targetPos }
            "ticks" ids { OuttakeArm.leftMotor.ticks }
            "effort" ids { OuttakeArm.leftMotor.lastWrite }
            "voltage" ids { Globals.robotVoltage }
        }

        RunCommand {
            val time = (System.nanoTime() - start) / 1e9
            println("$time, ${OuttakeArm.angle}")
        }.schedule()
        Telemetry.justUpdate().schedule()
    }
}
