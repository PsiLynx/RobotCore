package org.firstinspires.ftc.teamcode.subsystem.intake

import com.acmerobotics.dashboard.config.Config

import kotlin.jvm.JvmField;
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemIo
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.abs

@Config
object IntakeConf {
    @JvmField var backOutMotorPow = -0.5
    @JvmField
    var backOutTime = 0.5
}

object Intake: Subsystem<Intake>() {




    override val io: Io get() = TODO("Not yet implemented")

    override fun update(deltaTime: Double) { }

    fun backOut() = DeferredCommand {
        run(motorPow = IntakeConf.backOutMotorPow).withTimeout(
            IntakeConf.backOutTime
        )
    }

    fun setPower(pow: Double) = run {
        io.setPower(pow)
    } withEnd {
        io.setPower(0.0)
    }

    fun run(
        blockerPos: Component.Opening = Component.Opening.CLOSED,
        motorPow: Double = 1.0
    ) = (
        run {
            io.setPower(motorPow)

            io.blockerPos =
                if(blockerPos == Component.Opening.OPEN) 0.51
                else 0.71
        }
        withEnd {
            io.setPower(0.0)
 }
    ) withName "In: run"

    fun reverse() = setPower(-1.0) withName "In: reverse"
    fun stop() = InstantCommand { io.setPower(0.0)}  withName "In: stop"

}
