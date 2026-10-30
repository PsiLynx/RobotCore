package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.robot.Robot
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.subsystem.telemetry.Telemetry

@TeleOp(group = "a")
class TuneFlywheelVel: CommandOpMode() {
    override fun postSelector() {
//        driver.rightBumper.onTrue(Shooter.io.flywheelRun {
//            it.motors.forEach {
//                it.compPower(FlywheelTargetState.target)
//            }
//        })
        driver.leftBumper.whileTrue(Intake.run())
        driver.rightTrigger.whileTrue(Robot.kickBalls())
        Telemetry.addAll {
            "vel" ids { Shooter.io.flywheelCurrentState.velocity.toDouble() }
        }
    }
}
@Config object FlywheelTargetState {
    @JvmField var target = 0.0
}