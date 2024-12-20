package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.subsystem.Telemetry

@Autonomous(name = "TestSlideHeight", group = "a")
class TestSlideHeight: CommandOpMode() {
    override fun init() {
        initialize()
        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("encoder 1") { Extendo.leftMotor.toString() }
        Telemetry.addFunction("encoder 2") { Extendo.rightMotor.toString() }

        Telemetry.justUpdate().schedule()

        InstantCommand {
            Arm.pitchUp()
            Claw.pitchDown()
            Claw.grab()
        }.schedule()
        val moveSlidesALittle = Extendo.runToPosition(480.0)

        val moveArmUp = (
                RunCommand(Extendo) { Extendo.setPower(0.5) } until { Extendo.position > 910} withEnd { Extendo.setPower(0.0)}
                        andThen WaitCommand(1)
                )

        ( moveSlidesALittle andThen WaitCommand(1) andThen moveArmUp andThen Extendo.justUpdate()
                ).schedule()
    }
}
