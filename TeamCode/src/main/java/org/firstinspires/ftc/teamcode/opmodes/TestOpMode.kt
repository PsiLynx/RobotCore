package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.yAxisTouchSensorName

@TeleOp(name = "test op mode")
@Disabled
class TestOpMode: OpMode() {
    override fun init(){
        val touchSensor = GlobalHardwareMap.get(
            RevTouchSensor::class.java,
            yAxisTouchSensorName
        )

        Telemetry.addAll {
            "pressed" ids touchSensor::isPressed
        }
        Telemetry.justUpdate().schedule()
    }

    override fun loop() {
        CommandScheduler.update()
    }
}