package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.Telemetry

@TeleOp
class TuneFlywheelVel: CommandOpMode() {
    override fun postSelector() {
        driver.rightBumper.onTrue(Flywheel.run {
            it.motors.forEach {
                it.compPower(FlywheelTargetState.target)
            }
        })
        driver.leftBumper.whileTrue(Intake.run())
        driver.rightTrigger.whileTrue(Robot.kickBalls())
        Telemetry.addAll {
            "vel" ids { Flywheel.currentState.velocity.toDouble() }
        }
    }
}
@Config object FlywheelTargetState {
    @JvmField var target = 0.0
}