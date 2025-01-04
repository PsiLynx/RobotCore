package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Motor
import org.ftc3825.util.flMotorName

@TeleOp(name = "test op mode")
class TestOpMode: LinearOpMode() {
    override fun runOpMode() {
        GlobalHardwareMap.hardwareMap = hardwareMap
        val motor = Motor(flMotorName, 435)
        motor.setPower(1.0)
        waitForStart()
    }
}