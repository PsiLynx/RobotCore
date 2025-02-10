package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.ClipIntake
import org.ftc3825.subsystem.SampleIntake

@TeleOp
class ClipperFlipperTuning: CommandOpMode() {
    override fun init() {
        initialize()
        ClipIntake.reset()
        val driver = Gamepad(gamepad1!!)

        driver.dpadLeft.onTrue(ClipIntake.flipBack())
        driver.dpadRight.onTrue(ClipIntake.flipForward())
        driver.dpadUp.onTrue(ClipIntake.pitchLeft())
        driver.dpadDown.onTrue(ClipIntake.pitchRight())

        driver.a.onTrue(ClipIntake.grab())
        driver.b.onTrue(ClipIntake.release())

        driver.leftBumper.onTrue(SampleIntake.toggleGrip())
    }
}