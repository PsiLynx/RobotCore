package org.firstinspires.ftc.teamcode.sim

import org.junit.Test
import org.firstinspires.ftc.teamcode.sim.WheeliPidTuner

class WheeliTest {
    @Test
    fun runWheeliSim() {
        val thing = WheeliPidTuner()
        thing.simulation()
    }
}