package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.TimedCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel

@TeleOp
class FlywheelStep: CommandOpMode() {
    override fun postSelector() {
        (
                    ( Flywheel.runAtVelocity(100.0) withTimeout 3 )
            andThen ( Flywheel.runAtVelocity(120.0) withTimeout 3 )
            andThen ( Flywheel.runAtVelocity(140.0) withTimeout 3 )
            andThen ( Flywheel.runAtVelocity(160.0) withTimeout 3 )
            andThen ( Flywheel.runAtVelocity(180.0) withTimeout 3 )
            andThen ( Flywheel.runAtVelocity(200.0) withTimeout 3 )
            andThen ( Flywheel.runAtVelocity(230.0) withTimeout 3 )
        ).schedule()
    }

}