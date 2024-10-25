package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Arm
import org.ftc3825.util.Pose2D
import org.ftc3825.command.internal.Trigger
import org.ftc3825.command.internal.CommandScheduler
import kotlin.math.abs

@TeleOp(name = "FEILD CENTRIC", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()

        OuttakeSlides.reset()
        //OuttakeSlides.motors.forEach { it.encoder?.reset() }
        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.release()
        }.schedule()

        Telemetry.telemetry = telemetry

        var driver = Gamepad(gamepad1!!)
        var operator = Gamepad(gamepad2!!)

        var scale = 1.0;
        Drivetrain.run {
            it.driveFeildCentric(Pose2D(
                  -driver.left_stick_y * scale,
                    driver.left_stick_x * scale,
                    driver.right_stick_x * scale
            ))
        }.schedule()

        driver.right_bumper.onTrue( InstantCommand { scale = 0.25; Unit } )
        driver.right_bumper.onFalse( InstantCommand { scale = 1.0; Unit } )


        driver.left_bumper.onTrue(
            InstantCommand { Claw.toggleGrip() }
        )

        driver.dpad_left.onTrue( InstantCommand { Claw.rollLeft() } )
        driver.dpad_down.onTrue( InstantCommand { Claw.rollCenter() } )
        driver.dpad_right.onTrue( InstantCommand { Claw.rollRight() } )

        Trigger { driver.left_trigger > 0.7 }.onTrue(
            RunCommand(OuttakeSlides) {
                if(OuttakeSlides.position > 160){
                    OuttakeSlides.setPower(-0.1)
                }
                else {
                    OuttakeSlides.setPower(1.0)
                }
                Arm.pitchDown()
                Claw.pitchDown()
                Claw.rollCenter()
            } until { abs(OuttakeSlides.position - 160) < 15 }
            withEnd { OuttakeSlides.setPower(0.1) }
        )

        driver.y.onTrue(
            InstantCommand {
                Arm.pitchUp()
                Claw.pitchUp()
                Claw.rollRight()
            }
        )
        driver.a.onTrue(
            RunCommand(OuttakeSlides) {
                if(OuttakeSlides.position > 1600){
                    OuttakeSlides.setPower(0.25)
                }
                else {
                    OuttakeSlides.setPower(1.0)
                }
            } until { abs(OuttakeSlides.position - 1600) < 15 }
                    withEnd { OuttakeSlides.setPower(0.25) }
        )

        Trigger { driver.right_trigger > 0.7 } .onTrue(
            RunCommand(OuttakeSlides) {
                if(OuttakeSlides.position < 15){
                    OuttakeSlides.setPower(0.1)
                }
                else {
                    OuttakeSlides.setPower(-0.2)
                }
            } until { abs(OuttakeSlides.position - 15) < 15 }
                    withEnd { OuttakeSlides.setPower(0.1) }
        )


        Telemetry.data = arrayListOf()
        Telemetry.lines = arrayListOf()

        Telemetry.addFunction("par") { Drivetrain.encoders[0].distance }
        Telemetry.addFunction("perp") { Drivetrain.encoders[1].distance }
        Telemetry.addFunction("position") { Drivetrain.position }
        Telemetry.addFunction("delta") { Drivetrain.delta }
        Telemetry.addFunction("rotated") { Drivetrain.delta rotatedBy -Drivetrain.position.heading }

        Telemetry.addFunction("claw is pinched") { Claw.pinched }
        Telemetry.addFunction("\n") { CommandScheduler.status() }

        
        Telemetry.justUpdate().schedule()
    }
}
