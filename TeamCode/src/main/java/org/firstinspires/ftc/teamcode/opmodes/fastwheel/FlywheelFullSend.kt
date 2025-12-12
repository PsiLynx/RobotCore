package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.log

@TeleOp()
class FlywheelFullSend: CommandOpMode() {
    override fun postSelector() {
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
        Flywheel.fullSend().schedule()
        //Kicker.justUpdate().schedule()

        Telemetry.addAll {
            "vel" ids Flywheel::velocity
        }
    }
}