package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw

@TeleOp(name = "servos", group = "a")
class Servos: LinearOpMode() {

    override fun runOpMode(){
        GlobalHardwareMap.hardwareMap = this.hardwareMap
        val test = Servo("outtake roll")

        waitForStart()

        while(!isStopRequested){
            //test.position = gamepad1.left_stick_x.toDouble()
        }

    }


}