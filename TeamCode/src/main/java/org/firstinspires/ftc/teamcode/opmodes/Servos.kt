package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.subsystem.Transfer
import org.firstinspires.ftc.teamcode.subsystem.Telemetry

@TeleOp(name = "servos", group = "a")
class Servos: CommandOpMode() {

    override fun afterSelect() {
        Telemetry.initialize(this.telemetry)
	RunCommand {
		Transfer.servo.position = driver.leftStick.x.toDouble()
	}.schedule()
	Telemetry.addAll {
	    //"pos" ids Transfer.servo::position
            "controller" ids { driver.leftStick.x.toDouble() }
	}
    }


}
