package org.firstinspires.ftc.teamcode.util

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.fakehardware.FakeGamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeTelemetry
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.sim.SimulatedPollen
import org.psilynx.psikit.core.Logger

class OpModeRunner(
    val opmode: OpMode,
    val afterResetHooks: List<CommandOpMode.() -> Unit> = listOf()
) {
    init {
        opmode.hardwareMap = FakeHardwareMap
        opmode.telemetry = FakeTelemetry()
        opmode.gamepad1 = FakeGamepad()
        opmode.gamepad2 = FakeGamepad()
        Globals.fake = true
    }

    /**
     * run the opmode, start to finish
     */
    fun run(deltaTime: Double = 0.01){

        if(opmode is CommandOpMode) {
            opmode.afterResetHooks.addAll(afterResetHooks)
            opmode.afterResetHooks.add {
                Logger.setTimeSource {
                    FakeTimer.time
                }
            }
            FakeTimer.loopInterval = deltaTime
            opmode.afterLoopHooks.add {
                SimulatedPollen.allBalls.map{it}.withIndex().forEach {
                    it.value.update(CommandScheduler.deltaTime)
                    this@OpModeRunner.log(
                        "pollen/${it.index}"
                    ) value it.value.pos
                    /*
                    log("artifact hist/${it.index}") value (
                        it.value.poseHist.toTypedArray()
                    )
                     */
                }
            }
            opmode.timer = FakeTimer()
        }

        if(opmode is LinearOpMode) opmode.runOpMode()
        else {
            opmode.init()
            repeat(100) { opmode.init_loop() }

            opmode.start()

            val seconds = if(
                opmode::class.annotations.find { it is Autonomous } != null
            ){ 30 } else { 120 }

            while (FakeTimer.time < seconds){ opmode.loop() }
            opmode.stop()
       }
    }
}
