package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.Logger

@TeleOp()
class FlywheelFullSend: CommandOpMode() {
    override fun initialize() {
        RunCommand {
//            (
//                    FakeHardwareMap.get(DcMotor::class.java, "m0")
//                            as FakeMotor
//                    ).setCurrentPosition(
//                    FakeHardwareMap
//                        .get(DcMotor::class.java, "m4")
//                        .currentPosition
//                )
//            println(
//                Logger.getTimestamp()
//            )
            sleep(20)
        }.schedule()
        Flywheel.run {
                it.motor.compPower(1.0)
                log("velocity") value - it.velocity * 2320 / 250
        }.schedule()
        Kicker.justUpdate().schedule()

        Telemetry.addAll {
            "vel" ids Flywheel::velocity
        }
    }
}