package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp
class ShooterTest: LinearOpMode() {
    override fun runOpMode() {
        val motor = this.hardwareMap.get(DcMotor::class.java, "m2")
        while(true){
            this.telemetry.addData("position", motor.currentPosition)
            this.telemetry.update()
        }
    }
}