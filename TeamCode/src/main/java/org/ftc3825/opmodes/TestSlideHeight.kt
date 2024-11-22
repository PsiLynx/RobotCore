package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.subsystem.OuttakeSlides
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
        Telemetry.addFunction("encoder 1") { OuttakeSlides.leftMotor.toString() }
        Telemetry.addFunction("encoder 2") { OuttakeSlides.rightMotor.toString() }

        Telemetry.justUpdate().schedule()

        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.grab()
        }.schedule()
        val moveSlidesALittle = OuttakeSlides.runToPosition(480.0)

        val moveArmUp = (
                RunCommand(OuttakeSlides) { OuttakeSlides.setPower(0.5) } until { OuttakeSlides.position > 910} withEnd { OuttakeSlides.setPower(0.0)}
                        andThen WaitCommand(1)
                )

        ( moveSlidesALittle andThen WaitCommand(1) andThen moveArmUp andThen OuttakeSlides.justUpdate()
                ).schedule()
    }
}
