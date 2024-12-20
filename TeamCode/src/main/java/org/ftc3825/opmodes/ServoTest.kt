package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Telemetry

@TeleOp(name = "Servo Test", group = "a")
class ServoTest: CommandOpMode() {
    override fun init() {
        initialize()

        Extendo.reset()
        Arm.reset()
        Claw.reset()
        Drivetrain.reset()
        Telemetry.reset()

        Telemetry.telemetry = telemetry!!
        Telemetry.justUpdate().schedule()

        //( Arm.pitchUp() parallelTo Claw.grab() ).schedule()
        Claw.justUpdate().schedule()
        var waitStart = 0L
        RunCommand {
            waitStart = System.nanoTime()
            while(System.nanoTime() - waitStart < 1e8 ) { }
        }.schedule()

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)
        driver.dpadUp.onTrue( Claw.pitchUp() )
        driver.dpadDown.onTrue( Claw.pitchDown() )

        Telemetry.addAll {
           "left trigger"  to { driver.leftTrigger }
            "slides"       to { Extendo.leftMotor.position }
            "claw"         to { Claw.pitch }
            "loop hz"    to { 1 / CommandScheduler.deltaTime }
            "\n".add()
            "" to { CommandScheduler.status() }
        }
    }
}