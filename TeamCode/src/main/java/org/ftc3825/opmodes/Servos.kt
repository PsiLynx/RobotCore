package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Servo
import org.ftc3825.component.Servo.Range.goBilda
import org.ftc3825.subsystem.OuttakeClaw

@TeleOp(name = "servos", group = "a")
class Servos: LinearOpMode() {

    override fun runOpMode(){
        GlobalHardwareMap.hardwareMap = hardwareMap
        val test = OuttakeClaw.pitchServo

        waitForStart()

        while(!isStopRequested){
            test.position = gamepad1.left_stick_x.toDouble()
        }

    }


}