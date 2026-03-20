package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.TimedCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel

@TeleOp(group = "a")
class FlywheelStep: CommandOpMode() {
    override fun postSelector() {
        (
                    ( Flywheel.runAtVelocity(100.0) withTimeout 2 )
            andThen ( Flywheel.runAtVelocity(140.0) withTimeout 2 )
            andThen ( Flywheel.runAtVelocity(180.0) withTimeout 2 )
            andThen ( Flywheel.runAtVelocity(220.0) withTimeout 2 )
            andThen ( Flywheel.runAtVelocity(260.0) withTimeout 2 )
            andThen ( Flywheel.runAtVelocity(300.0) withTimeout 2 )
            andThen ( Flywheel.runAtVelocity(340.0) withTimeout 2 )
        ).schedule()
    }

}