package org.ftc3825.opmodes

import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.component.QuadratureEncoder
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.flMotorName
import org.ftc3825.util.xAxisTouchSensorName
import org.ftc3825.util.yAxisTouchSensorName

@TeleOp(name = "test op mode")
@Disabled
class TestOpMode: CommandOpMode() {
    override fun initialize(){
        val touchSensor = GlobalHardwareMap.get(
            RevTouchSensor::class.java,
            yAxisTouchSensorName
        )

        Telemetry.addAll {
            "pressed" ids touchSensor::isPressed
        }
        Telemetry.justUpdate().schedule()
    }
}