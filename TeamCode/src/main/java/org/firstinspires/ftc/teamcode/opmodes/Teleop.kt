package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
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
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun initialize() {


        var slowMode = false
        fun transMul() = if(slowMode) 0.25 else 1.0
        fun rotMul() = if(slowMode) 0.5 else 1.0

        Drivetrain.ensurePinpointSetup()
        InstantCommand {
            println("all hubs: ")
            println(this.allHubs.joinToString())
            println("^^^")
        }.schedule()

        Drivetrain.justUpdate().schedule()


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
        //dtControl.schedule()


        driver.apply {
//            dpadUp
//                .whileTrue(Extendo.setPowerCommand(0.0, 0.4))
//                .onFalse(operatorControl)
//            dpadDown
//                .whileTrue(Extendo.setPowerCommand(0.0, -0.4))
//                .onFalse(operatorControl)
//            dpadLeft
//                .whileTrue(Extendo.setPowerCommand(-0.8, 0.0))
//                .onFalse(operatorControl)
//            dpadRight
//                .whileTrue(Extendo.setPowerCommand(0.8, 0.0))
//                .onFalse(operatorControl)

        }

        operator.apply {
            x.whileTrue(
                Drivetrain.run {
                    it.setWeightedDrivePower(
                        driver.leftStick.y.sq,
                        driver.leftStick.x.sq,
                        -driver.rightStick.x.sq
                    )
                }
            ).onFalse(Drivetrain.resetToCorner(dtControl))

        }


        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "pinpoint" ids Drivetrain.pinpoint.hardwareDevice::getPosition
            "" ids CommandScheduler::status
        }
    }
}
