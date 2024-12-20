package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo

@TeleOp(name = "servos", group = "a")
class Servos: LinearOpMode() {

    override fun runOpMode(){
        val roll = hardwareMap.get(Servo::class.java, "roll")

        waitForStart()

        roll.position = 0.0

        sleep(1000)

        roll.position = 0.5

    }


}