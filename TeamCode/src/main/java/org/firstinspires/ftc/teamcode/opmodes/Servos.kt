package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.subsystem.Telemetry

@TeleOp(name = "servos", group = "a")
class Servos: CommandOpMode() {

    override fun initialize() {
        Telemetry.initialize(this.telemetry)
	RunCommand {
		Kicker.servo.position = driver.leftStick.x.toDouble()
	}.schedule()
	Telemetry.addAll {
	    "pos" ids Kicker.servo::position
            "controller" ids { driver.leftStick.x.toDouble() }
	}
    }


}
