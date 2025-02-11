package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.ftc3825.command.TeleopDrivePowers
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.CyclicalCommand
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.Trigger
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.command.transfer
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.ExtendoConf
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.degrees
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Vector2D
import kotlin.math.PI

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun init() {
        initialize()

        SampleIntake.reset()
        OuttakeClaw.reset()
        Drivetrain.reset()
        OuttakeArm.reset()
        Extendo.reset()

        Drivetrain.pinpoint.resetPosAndIMU()
        Drivetrain.position = Pose2D(0, 0, PI / 2)

        val driver = Gamepad(gamepad1!!)
        val operator = Gamepad(gamepad2!!)

        var slowMode = false
        fun transMul() = if(slowMode) 0.25 else 1.0
        fun rotMul() = if(slowMode) 0.5 else 1.0

//        TeleopDrivePowers(
//            { -driver.leftStickYSq * transMul() },
//            { driver.leftStickXSq * transMul() },
//            { -driver.rightStickXSq * rotMul() }
//        ).schedule()
        Drivetrain.run {
            it.setWeightedDrivePower(
                -driver.leftStickYSq.toDouble(),
                 driver.leftStickXSq.toDouble(),
                -driver.rightStickXSq.toDouble(),
            )
        }.schedule()

        val armSM = CyclicalCommand(
            ( OuttakeArm.runToPosition(degrees(150)) withTimeout(0.5) )
            andThen OuttakeClaw.release()
            andThen WaitCommand(0.3)
            andThen Command.parallel(
                OuttakeClaw.release(),
                OuttakeClaw.rollDown(),
                OuttakeClaw.wallPitch(),
                OuttakeArm.wallAngle()
            ),

            OuttakeClaw.grab()
            andThen WaitCommand(0.3)
            andThen Command.parallel(
                OuttakeClaw.outtakePitch(),
                OuttakeArm.outtakeAngle(),
                WaitCommand(0.5) andThen OuttakeClaw.rollUp(),
            )
        )

        val intakePitchSm = CyclicalCommand(
            SampleIntake.pitchBack() parallelTo SampleIntake.rollLeft(),
            SampleIntake.pitchDown() parallelTo SampleIntake.rollCenter(),
        )

        val operatorControl = Extendo.run {
            it.setPower(
                Vector2D(operator.leftStickXSq, operator.leftStickYSq)
            )
        }
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
        Trigger { driver.leftTrigger  > 0.7 || operator.leftTrigger > 0.7 }.onTrue(
            intakePitchSm.nextCommand()
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
            "pos" ids { Drivetrain.position }
            "vel" ids Drivetrain::velocity
            "extendo" ids Extendo::position
            "raw heading vel" ids { Drivetrain.pinpoint.velocity.getHeading(AngleUnit.RADIANS) }
            "outtake arm angle" ids OuttakeArm::angle
            "outtake arm effort" ids OuttakeArm.leftMotor::lastWrite
            "outtake arm target" ids OuttakeArm.leftMotor::setpoint
            "" ids CommandScheduler::status
        }
    }
}