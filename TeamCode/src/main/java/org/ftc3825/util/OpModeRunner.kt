package org.ftc3825.util

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.fakehardware.FakeTelemetry
import org.ftc3825.sim.SimConstants.timeStep
import org.ftc3825.sim.TestClass

class OpModeRunner(
    val opmode: OpMode,
    val afterInit: (OpMode) -> Boolean = {true},
    val assertAfterExecute: (OpMode) -> Boolean = {true},
    val assertEveryLoop: (OpMode) -> Boolean = {true},
): TestClass() {
    init {
        opmode.hardwareMap = FakeHardwareMap
        opmode.telemetry = FakeTelemetry()
    }

    fun run(){
        opmode.init()
        repeat(100) { opmode.init_loop() }

        assert(afterInit(opmode))

        opmode.start()

        val seconds = if(opmode::class.annotations.find { it is Autonomous } != null){
            30
        } else {
            120
        }

        repeat((seconds / timeStep).toInt()) {
            opmode.loop()
            assert(assertEveryLoop(opmode))
        }
        opmode.stop()

        assert(assertAfterExecute(opmode))
    }
}
