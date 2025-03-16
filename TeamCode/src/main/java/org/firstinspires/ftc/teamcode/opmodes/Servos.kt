package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.component.Servo

@TeleOp(name = "servos", group = "a")
class Servos: LinearOpMode() {

    override fun runOpMode(){
        val test = hardwareMap.get(Servo::class.java, "outtake roll")

        waitForStart()

        while(!isStopRequested){
            test.position = gamepad1.left_stick_x.toDouble()
        }

    }


}