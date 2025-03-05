package org.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.teamcode.command.TeleopDrivePowers
import org.teamcode.command.internal.Command
import org.teamcode.command.internal.CommandScheduler
import org.teamcode.command.internal.CyclicalCommand
import org.teamcode.command.internal.InstantCommand
import org.teamcode.command.internal.Trigger
import org.teamcode.command.internal.WaitCommand
import org.teamcode.component.Gamepad
import org.teamcode.subsystem.Drivetrain
import org.teamcode.subsystem.Extendo
import org.teamcode.subsystem.OuttakeArm
import org.teamcode.subsystem.OuttakeClaw
import org.teamcode.subsystem.SampleIntake
import org.teamcode.subsystem.Telemetry
import org.teamcode.util.degrees
import org.teamcode.util.geometry.Vector2D
import kotlin.math.PI

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun initialize() {
//        SampleIntake.reset()
//        OuttakeClaw.reset()
//        Drivetrain.reset()
//        OuttakeArm.reset()
//        Extendo.reset()

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)

        var slowMode = false
        fun transMul() = if(slowMode) 0.25 else 1.0
        fun rotMul() = if(slowMode) 0.5 else 1.0

        TeleopDrivePowers(
            { -driver.leftStickYSq * transMul() },
            { driver.leftStickXSq * transMul() },
            { -driver.rightStickXSq * rotMul() }
        ).schedule()

        val armSM = CyclicalCommand(
            Command.parallel(
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeArm.wallAngle() until { false }
            ) withName "intake position",

            OuttakeClaw.grab()
            andThen WaitCommand(0.5)
            andThen Command.parallel(
                OuttakeClaw.outtakePitch(),
                OuttakeArm.outtakeAngle(),
                WaitCommand(0.15) andThen OuttakeClaw.rollUp(),
            ) andThen OuttakeArm.justUpdate() withName "intake",

            (
                OuttakeArm.runToPosition(degrees(140)) withTimeout(0.3)
                andThen OuttakeClaw.release()
            ) andThen OuttakeArm.justUpdate() withName "outtake"
        )

        val intakePitchSm = CyclicalCommand(
            SampleIntake.pitchBack() parallelTo SampleIntake.rollLeft(),
            SampleIntake.pitchDown() parallelTo SampleIntake.rollCenter(),
        )

        val operatorControl = Extendo.run {
            it.setPower(
                Vector2D(operator.leftStickXSq, -operator.leftStickYSq)
            )
        }
        operatorControl.schedule()
        driver.dpadUp
            .whileTrue( Extendo.setPowerCommand(0.0, 0.5))
            .onFalse(operatorControl)
        driver.dpadDown
            .whileTrue( Extendo.setPowerCommand(0.0, -0.5))
            .onFalse(operatorControl)
        driver.dpadLeft
            .whileTrue( Extendo.setPowerCommand(-1.0, 0.0))
            .onFalse(operatorControl)
        driver.dpadRight
            .whileTrue( Extendo.setPowerCommand(1.0, 0.0))
            .onFalse(operatorControl)

        driver.rightBumper
            .onTrue ( InstantCommand { slowMode = true  } )
            .onFalse( InstantCommand { slowMode = false } )

        //driver.leftBumper.onTrue(intakeSample)
        driver.leftBumper.onTrue(SampleIntake.toggleGrip())

        Trigger { driver.rightTrigger > 0.7 }.onTrue( armSM.nextCommand() )
        Trigger {
            driver.leftTrigger  > 0.7 || operator.leftTrigger > 0.7
        }.onTrue(
            intakePitchSm.nextCommand()
        )
        driver.a.onTrue(
            Command.parallel(
                OuttakeClaw.outtakePitch(),
                OuttakeArm.outtakeAngle(),
                WaitCommand(0.15) andThen OuttakeClaw.rollUp(),
            )
        )

//        driver.a.onTrue(
//            Command.parallel(
//                SampleIntake.pitchBack(),
//                SampleIntake.looselyHold(),
//                (
//                    SampleIntake.rollCenter()
//                    andThen WaitCommand(0.5)
//                    andThen (
//                        SampleIntake.grab()
//                        parallelTo SampleIntake.rollBack()
//                    )
//                ),
//                OuttakeClaw.release(),
//                OuttakeClaw.intakePitch(),
//                OuttakeClaw.rollDown(),
//                OuttakeArm.runToPosition(degrees(210)),
//                Extendo.transferX()
//            )
//            andThen (
//                Extendo.setY(ExtendoConf.transferY)
//            )
//            andThen SampleIntake.pitchTransfer()
//            andThen OuttakeArm.transferAngle()
//            andThen OuttakeClaw.grab()
//            andThen WaitCommand(0.5) //TODO: tune timeout
//            andThen SampleIntake.release()
//            andThen SampleIntake.pitchBack()
//        )

        driver.y.onTrue( SampleIntake.rollCenter() )
        driver.x.onTrue( SampleIntake.nudgeLeft() )
        driver.b.onTrue( SampleIntake.nudgeRight() )

        operator.y.onTrue( SampleIntake.rollCenter() )
        operator.x.onTrue( SampleIntake.nudgeLeft() )
        operator.b.onTrue( SampleIntake.nudgeRight() )

        operator.dpadUp.onTrue(OuttakeClaw.rollUp())
        operator.dpadDown.onTrue(OuttakeClaw.rollDown())

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "extendo" ids Extendo::position
            "outtake arm angle" ids { OuttakeArm.angle / PI * 180 }
            "outtake arm setPoint" ids { OuttakeArm.leftMotor.setpoint / PI * 180 }
            "outtake arm effort" ids OuttakeArm.leftMotor::lastWrite
            "" ids CommandScheduler::status
        }
    }
}