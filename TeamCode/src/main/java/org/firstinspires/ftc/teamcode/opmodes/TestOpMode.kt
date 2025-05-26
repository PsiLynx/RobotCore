package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Telemetry

@TeleOp(name = "test op mode")
@Disabled
class TestOpMode: OpMode() {
    override fun init(){
        val touchSensor = HardwareMap.yAxisTouchSensor()

        Telemetry.addAll {
            "pressed" ids touchSensor::pressed
        }
        Telemetry.justUpdate().schedule()
    }

    override fun loop() {
        CommandScheduler.update()
    }
}