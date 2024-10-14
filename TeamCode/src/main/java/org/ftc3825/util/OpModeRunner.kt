package org.ftc3825.util

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.sim.timeStep
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.command.internal.CommandScheduler

class OpModeRunner(
    val opmode: OpMode,
    val afterInit: (OpMode) -> Boolean = {true},
    val assertAfterExecute: (OpMode) -> Boolean = {true}
): TestClass() {
    init {
        opmode.hardwareMap = FakeHardwareMap
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
        }
        opmode.stop()

        assert(assertAfterExecute(opmode))
    }
}
