package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.gvf.Builder
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain

fun intake(
    pathCommand: RamseteCommand,
    headingLock: Boolean = true,
    headLockTimeout: Double = 0.5,
): Command {
    return (
        If({headingLock}, (
            TankDrivetrain.headingLock(pathCommand.path[0].targetHeading(0.0))
            withTimeout headLockTimeout
        )) andThen (
            Intake.run()
            racesWith pathCommand
        )
    )
}

fun shoot(
    pathCommand: RamseteCommand
) = (
    ShootingStateOTM() parallelTo (
        (
            Intake.run(motorPow = 0.5)
            racesWith pathCommand
        ) andThen (
            WaitUntilCommand(Robot::readyToShoot)
            withTimeout 1
            andThen Robot.kickBalls()
        )
    )
)