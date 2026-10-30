package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.subsystem.telemetry.Telemetry

@TeleOp(group = "a")
class JustUpdateTurret: CommandOpMode() {
    override fun postSelector() {
        Shooter.io.turretUsingFeedback = false
        Shooter.justUpdate().schedule()
    }
}