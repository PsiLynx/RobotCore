package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Motor
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.flMotorName

@TeleOp(name = "test op mode")
class TestOpMode: CommandOpMode() {
    override fun init(){
        initialize()
        val motor = Motor("backLeft", 435)
        motor.useInternalEncoder()
        RunCommand {
            motor.update(0.1)
        }.schedule()
        Telemetry.addAll {
            "pos" ids { motor.encoder?.distance ?: "no encoder" }
        }

    }
}