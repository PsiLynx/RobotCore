package org.firstinspires.ftc.teamcode.subsystem.transfer

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
object TransferConf {
    @JvmField var backOutMotorPow = -0.5
    @JvmField
    var backOutTime = 0.5
}

object Transfer: Subsystem<Transfer, Io>(RealIo(), FakeIo()) {

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
            if(blockerPos == Component.Opening.OPEN) {
                io.setBlockerPos(0.43)
            }
            else {
                io.setBlockerPos(0.35)
            }
            io.setPower(motorPow)

        }
        withEnd {
            io.setPower(0.0)
 }
    ) withName "In: run"

    fun reverse() = setPower(-1.0) withName "In: reverse"
    fun stop() = InstantCommand { io.setPower(0.0)}  withName "In: stop"

}
