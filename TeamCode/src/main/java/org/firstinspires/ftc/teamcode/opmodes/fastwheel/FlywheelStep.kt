package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.TimedCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter

@TeleOp(group = "a")
class FlywheelStep: CommandOpMode() {
    override fun postSelector() {
        (
                    ( Shooter.flywheelRunAtVelocity(100.0) withTimeout 2 )
            andThen ( Shooter.flywheelRunAtVelocity(140.0) withTimeout 2 )
            andThen ( Shooter.flywheelRunAtVelocity(180.0) withTimeout 2 )
            andThen ( Shooter.flywheelRunAtVelocity(220.0) withTimeout 2 )
            andThen ( Shooter.flywheelRunAtVelocity(260.0) withTimeout 2 )
            andThen ( Shooter.flywheelRunAtVelocity(300.0) withTimeout 2 )
            andThen ( Shooter.flywheelRunAtVelocity(340.0) withTimeout 2 )
        ).schedule()
    }

}