package org.ftc3825.util

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.sim.DataAnalyzer
import org.ftc3825.util.Globals.State.Testing

open class TestClass {
    val hardwareMap = FakeHardwareMap

    init {
        if(!initialized) {
            Globals.state = Testing

            CommandScheduler.init(hardwareMap)

            CommandScheduler.update()
            CommandScheduler.update()

            DataAnalyzer.loadTestData()
            DataAnalyzer.analyze()
        }
        initialized = true

        FakeHardwareMap.allDeviceMappings.forEach {mapping ->
            mapping.forEach {
                it.resetDeviceConfigurationForOpMode()
            }
        }
        println("initialized")
    }
    companion object{
        var initialized = false
    }
}