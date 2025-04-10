package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.cycle
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RepeatCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Extendo
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.SampleIntake
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
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
            { - driver.leftStick.y.sq  * transMul() },
            {   driver.leftStick.x.sq  * transMul() },
            {
                Vector2D(
                    driver.rightStick.x,
                    -driver.rightStick.y
                )
            }
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
                OuttakeArm.outtakeAngle() until { false },
                WaitCommand(0.15) andThen OuttakeClaw.rollUp(),
            ) andThen OuttakeArm.justUpdate() withName "intake",

        )

        val intakePitchSm = CyclicalCommand(
            SampleIntake.pitchBack() parallelTo SampleIntake.rollLeft(),
            SampleIntake.pitchDown() parallelTo SampleIntake.rollCenter(),
        )

        val operatorControl = Extendo.run {
            it.setPower(
                Vector2D(operator.leftStick.x.sq, -operator.leftStick.y.sq)
            )
        }
        operatorControl.schedule()

        driver.apply {
            dpadUp
                .whileTrue(Extendo.setPowerCommand(0.0, 0.5))
                .onFalse(operatorControl)
            dpadDown
                .whileTrue(Extendo.setPowerCommand(0.0, -0.5))
                .onFalse(operatorControl)
            dpadLeft
                .whileTrue(Extendo.setPowerCommand(-1.0, 0.0))
                .onFalse(operatorControl)
            dpadRight
                .whileTrue(Extendo.setPowerCommand(1.0, 0.0))
                .onFalse(operatorControl)

            rightBumper
                .onTrue(InstantCommand { slowMode = true })
                .onFalse(InstantCommand { slowMode = false })

            leftBumper.onTrue(SampleIntake.toggleGrip())

            rightTrigger.onTrue(armSM.nextCommand())
            leftTrigger.onTrue(intakePitchSm.nextCommand())

            leftStick.onTrue(RepeatCommand(cycle(), 15))

            y.onTrue(SampleIntake.rollCenter())
            x.onTrue(SampleIntake.nudgeLeft())
            b.onTrue(SampleIntake.nudgeRight())
        }

        operator.apply {
            y.onTrue(SampleIntake.rollCenter())
            x.onTrue(SampleIntake.nudgeLeft())
            b.onTrue(SampleIntake.nudgeRight())

            dpadUp.onTrue(OuttakeClaw.rollUp())
            dpadDown.onTrue(OuttakeClaw.rollDown())
        }

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "vel" ids Drivetrain::velocity
            "target" ids Drivetrain::targetHeading
            "extendo" ids Extendo::position
            "outtake arm angle" ids { OuttakeArm.angle / PI * 180 }
            "outtake arm setPoint" ids { OuttakeArm.leftMotor.setpoint / PI * 180 }
            "outtake arm effort" ids OuttakeArm.leftMotor::lastWrite
            "" ids CommandScheduler::status
        }
    }
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
}