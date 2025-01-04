package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Servo
import org.ftc3825.component.Servo.Range.goBilda

@TeleOp(name = "servos", group = "a")
class Servos: LinearOpMode() {

    override fun runOpMode(){
        GlobalHardwareMap.hardwareMap = hardwareMap
        val test = Servo("test", goBilda)

        waitForStart()

        test.position = 0.5

        sleep(1000)

        test.position = 0.4

        sleep(1000)

        test.position = 0.2

        sleep(1000)

        test.position = 0.0

        sleep((1e9).toLong())

    }


}