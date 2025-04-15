package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
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
import org.firstinspires.ftc.teamcode.subsystem.OuttakeClaw
import org.firstinspires.ftc.teamcode.subsystem.SampleIntake
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import kotlin.math.PI

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun initialize() {

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)

        var slowMode = false
        fun transMul() = if(slowMode) 0.25 else 1.0
        fun rotMul() = if(slowMode) 0.5 else 1.0

        fun cmd() = (
            hang(
                path {
                    start(40, -66)
                    lineTo(5, -50, forward)

                    start(-5, -45)
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

        val armSM = CyclicalCommand(
            Command.parallel(
	    	OuttakeClaw.release(),
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
		* ( if(operator.leftBumper.supplier.asBoolean) 0.3 else 1.0 )
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

            y.onTrue(autoCycleCommand)
            b.onTrue(
                InstantCommand { CommandScheduler.end(autoCycleCommand) }
                andThen dtControl
            )
            x.whileTrue(
                Drivetrain.run {
                    it.setWeightedDrivePower(
                        -driver.leftStick.y.toDouble(),
                        driver.leftStick.x.toDouble(),
                        -driver.rightStick.x.toDouble(),
                    )
                }
            ).onFalse( Drivetrain.resetToCorner(next = dtControl) )

        }

        operator.apply {
            a.onTrue(OuttakeClaw.toggleGrip())

            y.onTrue(
                OuttakeArm.runToPosition(degrees(140)) withTimeout(0.5)
                andThen OuttakeClaw.release()
            )

            leftTrigger.onTrue(intakePitchSm.nextCommand())

            rightTrigger.onTrue(SampleIntake.grab())
            rightBumper.onTrue(SampleIntake.release())


            dpadUp.onTrue(OuttakeClaw.rollUp())
            dpadDown.onTrue(OuttakeClaw.rollDown())

            Trigger { rightStick.mag > 0.9 }.whileTrue(
                RunCommand { SampleIntake.setAngle(rightStick.theta) }
            )
        }

        OuttakeArm.justUpdate().schedule()

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "extendo" ids Extendo::position
            "outtake arm angle" ids { OuttakeArm.angle }
            "" ids CommandScheduler::status
        }
    }
}
