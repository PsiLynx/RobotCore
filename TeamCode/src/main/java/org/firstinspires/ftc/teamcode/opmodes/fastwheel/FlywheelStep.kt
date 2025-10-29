package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.TimedCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel

@TeleOp
class FlywheelStep: CommandOpMode() {
    override fun afterSelect() {
        (
                    ( Flywheel.setPower(0.5) withTimeout 3 )
            andThen ( Flywheel.setPower(0.6) withTimeout 3 )
            andThen ( Flywheel.setPower(0.7) withTimeout 3 )
            andThen ( Flywheel.setPower(0.8) withTimeout 3 )
            andThen ( Flywheel.setPower(0.9) withTimeout 3 )
            andThen ( Flywheel.setPower(1.0) withTimeout 3 )
        ).schedule()
    }

}