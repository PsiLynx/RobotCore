package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

@TeleOp(group = "a")
class TuneTurret: CommandOpMode() {
    override fun postSelector() {
        Turret.run { it.usingFeedback = true }.schedule()
        RunCommand { Thread.sleep(10) }.schedule()
        driver.apply {
            y.onTrue(InstantCommand {
                Turret.targetState += PvState(Rotation2D(PI/6), Rotation2D())
            })
            a.onTrue(InstantCommand {
                Turret.targetState -= PvState(Rotation2D(PI/6), Rotation2D())
            })
        }
    }
}