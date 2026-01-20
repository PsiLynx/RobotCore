package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.Turret

@TeleOp
class JustUpdateTurret: CommandOpMode() {
    override fun postSelector() {
        Turret.usingFeedback = false
        Turret.justUpdate().schedule()
    }
}