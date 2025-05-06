package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ArmSM
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.hang
import org.firstinspires.ftc.teamcode.command.intake
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.Trigger
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Extendo
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm.leftMotor
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClawConf
import org.firstinspires.ftc.teamcode.subsystem.SampleIntake
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun initialize() {

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)

        var slowMode = false
        fun transMul() = if(slowMode) 0.25 else 1.0
        fun rotMul() = if(slowMode) 0.5 else 1.0

        //Drivetrain.ensurePinpointSetup()
        //OuttakeArm.ensureZeroed()

        (
            WaitCommand(0.1)
            andThen (
                SampleIntake.SM.nextCommand()
                parallelTo SampleIntake.toggleGrip()
            )
        ).schedule()

        fun cmd() = (
            hang(
                path {
                    start(40, -66)
                    lineTo(10, -50, forward)

                    start(-3, -45)
                    lineTo(0, -25, forward)
                    endVel(10.0)
                }
            )
            andThen intake(path {
                start(3, -29)
                lineTo(0, -33, forward)
                lineTo(40, -66.2, forward)
            })
        )
        val autoCycleCommand = (
            OuttakeClaw.release()
            andThen intake()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
            andThen cmd()
        )
        val dtControl = TeleopDrivePowers(
            { - driver.leftStick.y.sq  * transMul() },
            {   driver.leftStick.x.sq  * transMul() },
            {
                Vector2D(
                    driver.rightStick.x,
                    -driver.rightStick.y
                )
            }
        )
        dtControl.schedule()

        val operatorControl = Extendo.run {
            it.setPower(
                Vector2D(operator.leftStick.x.sq, -operator.leftStick.y.sq)
		* ( if(operator.leftBumper.supplier.asBoolean) 0.3 else 1.0 )
            )
        }
        operatorControl.schedule()

        driver.apply {
            dpadUp
                .whileTrue(Extendo.setPowerCommand(0.0, 0.4))
                .onFalse(operatorControl)
            dpadDown
                .whileTrue(Extendo.setPowerCommand(0.0, -0.4))
                .onFalse(operatorControl)
            dpadLeft
                .whileTrue(Extendo.setPowerCommand(-0.8, 0.0))
                .onFalse(operatorControl)
            dpadRight
                .whileTrue(Extendo.setPowerCommand(0.8, 0.0))
                .onFalse(operatorControl)

            leftBumper.onTrue(SampleIntake.toggleGrip())

            rightTrigger.onTrue(ArmSM)
            leftTrigger.onTrue(SampleIntake.SM.nextCommand())

            y.onTrue(SampleIntake.rollCenter())
            x.onTrue(SampleIntake.nudgeLeft())
            b.onTrue(SampleIntake.nudgeRight())

        }

        operator.apply {
            y.onTrue(
                OuttakeArm.runToPosition(degrees(140)) withTimeout(0.5)
                andThen OuttakeClaw.release()
            )
            x.whileTrue(
                Drivetrain.run {
                    it.setWeightedDrivePower(
                        driver.leftStick.y.sq,
                        driver.leftStick.x.sq,
                        -driver.rightStick.x.sq
                    )
                }
            ).onFalse(Drivetrain.resetToCorner(dtControl))

            leftTrigger.onTrue(SampleIntake.SM.nextCommand())

            rightTrigger.onTrue(SampleIntake.grab())
            rightBumper.onTrue(SampleIntake.release())

            dpadUp.whileTrue(   OuttakeArm.setPowerCommand(0.5)  )
            dpadDown.whileTrue( OuttakeArm.setPowerCommand(-0.5)  )



            Trigger { rightStick.mag > 0.9 }.whileTrue(
                RunCommand { SampleIntake.setAngle(rightStick.theta) }
            )
        }

        OuttakeArm.justUpdate().schedule()

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "pinpoint" ids Drivetrain.pinpoint.hardwareDevice::getPosition
            "extendo" ids Extendo::position
            "state" ids { ArmSM.current.name() }
            "intake state #" ids { SampleIntake.SM.currentIndex }
            "outtake arm angle" ids { OuttakeArm.angle }
            "quadrature" ids { leftMotor.encoder!!.pos }
            "" ids CommandScheduler::status
        }
    }
}
