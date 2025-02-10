package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.TeleopDrivePowers
import org.ftc3825.command.clip
import org.ftc3825.command.intakeSample
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CyclicalCommand
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.Trigger
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.command.transfer
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.ClipIntake
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.geometry.Pose2D
import kotlin.math.PI

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun init() {
        initialize()

        SampleIntake.reset()
        OuttakeClaw.reset()
        Drivetrain.reset()
        ClipIntake.reset()
        OuttakeArm.reset()
        Extendo.reset()

        Drivetrain.position = Pose2D(10, 10, PI / 2)

        val driver = Gamepad(gamepad1!!)

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
            Command.parallel(
                OuttakeClaw.release(),
                OuttakeClaw.rollDown(),
                OuttakeClaw.intakePitch(),
                OuttakeArm.transferAngle()
            ),

            Command.parallel(
                OuttakeClaw.rollUp(),
                OuttakeClaw.outtakePitch(),
                OuttakeArm.outtakeAngle()
            ) andThen OuttakeClaw.release()
        )
        armSM.schedule()

        val intakePitchSm = CyclicalCommand(
            SampleIntake.pitchDown(),
            SampleIntake.pitchBack()
        )
        intakePitchSm.schedule()

        driver.dpadUp.   whileTrue( Extendo.setPowerCommand(  0.0,   1.0))
        driver.dpadDown. whileTrue( Extendo.setPowerCommand(  0.0, - 1.0))
        driver.dpadLeft. whileTrue( Extendo.setPowerCommand(- 1.0,   0.0))
        driver.dpadRight.whileTrue( Extendo.setPowerCommand(  1.0,   0.0))

        driver.rightBumper
            .onTrue ( InstantCommand { slowMode = true  } )
            .onFalse( InstantCommand { slowMode = false } )

        //driver.leftBumper.onTrue(intakeSample)
        driver.leftBumper.onTrue(SampleIntake.toggleGrip())

        Trigger { driver.rightTrigger > 0.7 }.onTrue( armSM.nextCommand() )
        Trigger { driver.leftTrigger  > 0.7 }.onTrue(
            intakePitchSm.nextCommand()
        )

        driver.y.onTrue( SampleIntake.rollCenter() )
        driver.x.onTrue( SampleIntake.nudgeLeft() )
        driver.b.onTrue( SampleIntake.nudgeRight() )

        driver.y.onTrue( clip andThen transfer )


        Telemetry.addAll {
            "pos" ids { Pose2D(Drivetrain.pinpoint.position) }
            "vel" ids Drivetrain::velocity
            "raw heading vel" ids { Drivetrain.pinpoint.headingVelocity }
            "left stick x" ids driver::leftStickX
            "left stick y" ids driver::leftStickY
            "right stick x" ids driver::rightStickX
        }
    }
}