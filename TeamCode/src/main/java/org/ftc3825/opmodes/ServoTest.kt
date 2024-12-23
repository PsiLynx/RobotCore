package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Telemetry

@TeleOp(name = "Servo Test", group = "a")
class ServoTest: CommandOpMode() {
    override fun init() {
        initialize()

        Extendo.reset()
        OuttakeArm.reset()
        OuttakeClaw.reset()
        Drivetrain.reset()
        Telemetry.reset()

        Telemetry.telemetry = telemetry!!
        Telemetry.justUpdate().schedule()

        //( Arm.pitchUp() parallelTo Claw.grab() ).schedule()
        OuttakeClaw.justUpdate().schedule()
        var waitStart = 0L
        RunCommand {
            waitStart = System.nanoTime()
            while(System.nanoTime() - waitStart < 1e8 ) { }
        }.schedule()

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)
        driver.dpadUp.onTrue( OuttakeClaw.pitchUp() )
        driver.dpadDown.onTrue( OuttakeClaw.pitchDown() )

        Telemetry.addAll {
           "left trigger" ids { driver.leftTrigger }
            "slides"      ids { Extendo.leftMotor.position }
            "claw"        ids { OuttakeClaw.pitch }
            "loop hz"     ids { 1 / CommandScheduler.deltaTime }
            "\n".add()
            "" to { CommandScheduler.status() }
        }
    }
}