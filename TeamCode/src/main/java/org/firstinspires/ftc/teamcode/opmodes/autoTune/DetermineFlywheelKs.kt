package org.firstinspires.ftc.teamcode.opmodes.autoTune

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs

@TeleOp(group = "autoTune")
class DetermineFlywheelKs: CommandOpMode() {
    /**
     * first = voltage
     * second = velocity
     */
    var dataPoints = arrayListOf<Pair<Double, Double>>()

    override fun postSelector() {
        (
            (
                Flywheel.run {
                    it.motors.forEach {
                        it.compPower(0.1 * Globals.currentTime)
                    }
                    dataPoints.add(
                        (0.1 * Globals.currentTime)
                        to Flywheel.currentState.velocity.toDouble()
                    )
                } until { Flywheel.currentState.velocity.toDouble() > 1 }
            )
            andThen InstantCommand {
                val ks = dataPoints.last { abs(it.second) < 0.01 }.first
                Telemetry.addLine { "flywheel ks: $ks" }
                dataPoints = arrayListOf()
            }
        ).schedule()

    }
}
