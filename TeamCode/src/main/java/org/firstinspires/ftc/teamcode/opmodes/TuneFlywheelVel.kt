package org.firstinspires.ftc.teamcode.opmodes

import android.R.attr.value
import android.R.attr.x
import android.R.attr.y
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.RobotLog.a
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.controller.pid.TunablePIDF
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import kotlin.reflect.full.findAnnotation

@TeleOp
class TuneFlywheelVel: CommandOpMode() {
    override fun initialize() {
        var max = Flywheel.MAX_VEL
        var min = 0.0

        driver.apply {
            a.onTrue(InstantCommand {
                max /= 2
                min /= 2
            })
            b.onTrue(InstantCommand {
                max /= 4
                min /= 4
            })
            x.onTrue(InstantCommand {
                max /= 6
                min /= 6
            })
            y.onTrue(InstantCommand {
                max /= 8
                min /= 8
            })
            (a and b and x and y).onFalse(InstantCommand {
                max = Flywheel.MAX_VEL
            })
            dpadUp.  onTrue( Flywheel.runAtVelocity{max} )
            dpadDown.onTrue( Flywheel.runAtVelocity{min} )
        }
    }
}