package org.firstinspires.ftc.teamcode.opmodes.autoTune

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs

@TeleOp(group = "autoTune")
class DetermineTankKs: CommandOpMode() {
    /**
     * first = voltage
     * second = velocity
     */
    var dataPoints = arrayListOf<Pair<Double, Double>>()

    override fun postSelector() {
        (
            (
                TankDrivetrain.run {
                    it.setWeightedDrivePower(drive = 0.1 * Globals.currentTime)
                    dataPoints.add(
                        (0.1 * Globals.currentTime)
                        to TankDrivetrain.velocity.vector.mag
                    )
                } until { TankDrivetrain.velocity.vector.mag > 1 }
            )
            andThen InstantCommand {
                val ks = dataPoints.last { abs(it.second) < 0.01 }.first
                Telemetry.addLine { "drive ks: $ks" }
                dataPoints = arrayListOf()
            }
            andThen (
                TankDrivetrain.run {
                    it.setWeightedDrivePower(turn = 0.1 * Globals.currentTime)
                    dataPoints.add(
                        (0.1 * Globals.currentTime)
                        to TankDrivetrain.velocity.heading.mag
                    )
                } until { TankDrivetrain.velocity.heading.mag > 1 }
            )
            andThen InstantCommand {
                val ks = dataPoints.last { abs(it.second) < 0.01 }.first
                Telemetry.addLine { "turn ks: $ks" }
                dataPoints = arrayListOf()
            }
        ).schedule()

    }
}