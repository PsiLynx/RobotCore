package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.LocalizerSubsystem
import org.ftc3825.subsystem.TelemetrySubsystem
import org.ftc3825.util.Pose2D

@TeleOp(name = "servos", group = "a")
class Servos: LinearOpMode() {

    override fun runOpMode(){
        var roll = hardwareMap.get(Servo::class.java, "roll")
        var claw = hardwareMap.get(Servo::class.java, "claw")

        waitForStart()

//        roll.position = 0.0
//        claw.position = 0.0

        //sleep(1000)

        roll.position = 0.5
        claw.position = 1.0

        sleep(10000)

    }


}