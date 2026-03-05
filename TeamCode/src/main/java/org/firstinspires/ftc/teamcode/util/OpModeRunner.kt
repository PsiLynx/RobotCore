package org.firstinspires.ftc.teamcode.util

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.fakehardware.FakeGamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeTelemetry
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.sim.TestClass

class OpModeRunner(
    val opmode: OpMode,
    val afterResetHooks: List<CommandOpMode.() -> Unit> = listOf()
) {
    init {
        opmode.hardwareMap = FakeHardwareMap
        opmode.telemetry = FakeTelemetry()
        opmode.gamepad1 = FakeGamepad()
        opmode.gamepad2 = FakeGamepad()
    }

    /**
     * run the opmode, start to finish
     */
    fun run(){

        if(opmode is CommandOpMode) {
            opmode.afterResetHooks.addAll(afterResetHooks)
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
